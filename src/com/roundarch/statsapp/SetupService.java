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
    public class SetupBinder extends Binder
    {
        SetupService getService()
        {
            return SetupService.this;
        }
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
     * do I really want to have multiple alarms? why not just one?
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
        loadSuccess = store.load();
        if (loadSuccess)
            Log.d(TAG, "store was loaded");
        else
            Log.d(TAG, "could not load store");

    }

    @Override public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "service start called");
        if (loadSuccess && !alarmIsSet)
        {   
            Log.d(TAG, "setting alarm in onStartCommand");
            setAlarm(alarmTime);
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
        //TODO anything else?
        store.save();
        store = null;
    }

    /**
     * Creates an APIConnection and hooks it up.
     *
     */
    public APIConnection createConnection(HashMap<String, Object> cMap)
    {
        APIConnection conn = store.genConn(cMap);
        store.save();
        Log.d(TAG, "created connection " + conn.getId());
        setAlarm(alarmTime);
        return conn;
    }

    public void modifyConnection(APIConnection conn)
    {
        Log.d(TAG, "modifying connection " + conn.getId());
        //not really sure what to make of this. TODO
        //send updating intent out? ask updaterservice to start up?
        //starting the updateservicem might be the way to go...
        //reset the alarm.
        setAlarm(alarmTime);
        store.save();
    }
    
    public void deleteConnection(APIConnection conn)
    {
        Log.d(TAG, "deleting connection " + conn.getId());
        //ok really don't know what to make of this. TODO 
        //might have to broadcast some sort of app widget intent that says delete widgets
        //with the id of this conn.
        // anyway:
        store.delete(conn.getId());
        store.save();
    }

    public APIConnection getConnection(int id)
    {
        return store.get(id);
    }

    public List<APIConnection> connectionList()
    {
        ArrayList<APIConnection> conns = new ArrayList<APIConnection>(store.size());
        for (Integer connId : store.idSet())
        {
            conns.add(store.get(connId));
        }

        return conns;
    }
    
    public int getAlarm()
    {
        return alarmTime;
    }

    protected void setStartedAlarm(long millisFromNow)
    {
        Intent started = new Intent(STARTED);
        alarmLauncher = PendingIntent.getBroadcast(this, 0, started, PendingIntent.FLAG_UPDATE_CURRENT);
        if (manager != null)
            manager.set(AlarmManager.RTC, (long)System.currentTimeMillis() + millisFromNow, alarmLauncher);
    }

    public void setAlarm(int seconds)
    {
        Log.d(TAG, "alarm set");
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

    public void clearAlarm()
    {
        Log.d(TAG, "alarm cleared");
        if (manager != null)
            manager.cancel(alarmLauncher);
        alarmIsSet = false;
    }
}
