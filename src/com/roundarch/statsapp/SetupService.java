package com.roundarch.statsapp;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import android.util.Log;
import android.os.IBinder;
import android.os.Binder;
import android.os.SystemClock;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.app.Service;
import android.app.AlarmManager;
import android.app.PendingIntent;

public class SetupService extends Service
{
    public static final String TAG = "com.roundarch.statsapp.SetupService";
    public static final String STARTED = "com.roundarch.statsapp.ACTION_SETUP_SERVICE_STARTED";
    public static final String CREATE = "com.roundarch.statsapp.ACTION_SETUP_CREATE_CONN";
    public static final String DELETE = "com.roundarch.statsapp.ACTION_SETUP_DELETE_CONN";
    public static final String DELETED = "com.roundarch.statsapp.ACTION_CONNECTION_DELETED";
    public static final String UPDATED = "com.roundarch.statsapp.ACTION_CONNECTION_UPDATED";

    public class SetupBinder extends Binder
    {
        SetupService getService()
        {
            return SetupService.this;
        }
    }

    public static HashMap<String, Object> getCMapFromIntent(Intent intent)
    {
        HashMap<String, Object> cMap = new HashMap<String, Object>();
        Bundle b = intent.getExtras();
        for (String k : b.keySet())
        {
            cMap.put(k, b.get(k));
        }
        return cMap;
    }

    protected ConnectionStore store;
    protected int alarmTime;
    //protected HashMap<Integer, PendingIntent> alarms;
    protected AlarmManager manager;
    protected Resources res;
    protected String prefsFile;
    protected String kUpdateTime;
    protected String storeFile;

    protected PendingIntent alarmLauncher;
    protected boolean alarmIsSet = false;
    protected boolean loadSuccess = false;

    private final SetupBinder mBinder = new SetupBinder();

    /**
     * Initial setup of service. 
     * Loads alarm time from shared prefs, loads a connectionstore.
     */
    @Override public void onCreate()
    {
        Log.d(TAG, "service created");
        res = getResources();
        prefsFile = res.getString(R.string.prefs_file);
        kUpdateTime = res.getString(R.string.kUpdateTime);
        storeFile = res.getString(R.string.store_file);
        int defUpdateTime = res.getInteger(R.integer.def_update_time);

        manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmTime = getSharedPreferences(prefsFile, MODE_PRIVATE).getInt(kUpdateTime, defUpdateTime);
        store = new ConnectionStore(this, storeFile);
        //load the store, and remember if it succeeded
        loadSuccess = store.load();
        if (loadSuccess)
            Log.d(TAG, "store was loaded");
        else
            Log.d(TAG, "could not load store");

    }

    /**
     * Make sure to start the service before binding to it.
     */
    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "service start called");
        if (loadSuccess && !alarmIsSet)
        {   
            Log.d(TAG, "setting alarm in onStartCommand");
            setAlarm(alarmTime);
        }
        String action = intent.getAction();
        if (action != null && action.equals(CREATE))
        {
            Log.d(TAG, "started: creating a new connection");
            createConnection(getCMapFromIntent(intent));
        }
        else if (action != null && action.equals(DELETE))
        {
            int id = intent.getIntExtra(APIConnection.kID, -1);
            Log.d(TAG, "started: deleting connection " + id);
            deleteConnection(getConnection(id));
        }
        return START_STICKY;
    }
    
    @Override public IBinder onBind(Intent intent)
    {
        Log.d(TAG, "service bound");
        if (loadSuccess && !alarmIsSet)
        {   
            Log.d(TAG, "setting alarm in onBind");
            setAlarm(alarmTime);
        }
        return mBinder;
    }

    @Override public void onDestroy()
    {
        Log.d(TAG, "service being destroyed");
        //disconnect alarm
        clearAlarm();
        //save the store and wipe it.
        store.save();
        store = null;
    }

    /**
     * Creates a new APIConnection in the store, and then resets the alarm so it
     * gets updated.
     */
    public APIConnection createConnection(HashMap<String, Object> cMap)
    {
        APIConnection conn = store.genConn(cMap);
        store.save();
        Log.d(TAG, "created connection " + conn.getId());
        setAlarm(alarmTime);
        return conn;
    }

    /**
     * Updates the connection. Saves the store, then reschedules
     * the update alarm. This way, the change in settings
     * will reach the rest of the app.
     */
    public void modifyConnection(APIConnection conn)
    {
        Log.d(TAG, "modifying connection " + conn.getId());
        //not really sure what to make of this. TODO
        //reset the alarm.
        store.save();
        setAlarm(alarmTime);
    }
    
    /**
     * Deletes the connection from the store. Broadcasts
     * an intent saying that this occurred.
     */
    public void deleteConnection(APIConnection conn)
    {
        Log.d(TAG, "deleting connection " + conn.getId());
        if (conn == null)
            return ;
        //delete
        store.delete(conn.getId());
        store.save();
        //broadcast deletion
        Intent deleted = new Intent(DELETED);
        deleted.putExtra(APIConnection.kID, conn.getId());
        sendBroadcast(deleted);
    }

    public APIConnection getConnection(int id)
    {
        return store.get(id);
    }

    /**
     * Returns a List of the apiconnections in the store at 
     * the moment this is called.
     */
    public List<APIConnection> connectionList()
    {
        ArrayList<APIConnection> conns = new ArrayList<APIConnection>(store.size());
        for (Integer connId : store.idSet())
        {
            conns.add(store.get(connId));
        }

        return conns;
    }
    
    /**
     * Returns amount of time between updates, in seconds
     */
    public int getAlarm()
    {
        return alarmTime;
    }

    /**
     * Sets the alarm to go off first within the second, then every seconds afterward.
     * Cancels the alarm if it's currently set. If the new time is different from the old,
     * stores the new time in the prefs file.
     */
    public void setAlarm(int seconds)
    {
        Log.d(TAG, "alarm set");
        if (alarmIsSet)
            clearAlarm();
        if (seconds != alarmTime)
        {
            alarmTime = seconds;
            getSharedPreferences(prefsFile, MODE_PRIVATE).edit().putInt(kUpdateTime, alarmTime).commit();
        }
        Intent triggerUpdate = new Intent(this, AlarmReceiver.class);
        alarmLauncher = PendingIntent.getBroadcast(this, 0, triggerUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null)
            manager.setRepeating(AlarmManager.RTC, (long)System.currentTimeMillis() + 100, (long)seconds * 1000, alarmLauncher);
        alarmIsSet = true;
    }

    /**
     * Cancels the current alarm.
     */
    public void clearAlarm()
    {
        Log.d(TAG, "alarm cleared");
        if (manager != null)
            manager.cancel(alarmLauncher);
        alarmIsSet = false;
    }
}
