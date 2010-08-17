package com.roundarch.statsapp;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.os.Bundle;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.util.Log;


public class DisplayActivity extends ListActivity
{
    public static final String TAG = "com.roundarch.statsapp.DisplayActivity";
    //TODO options menu ... 
    

    public static class AdapterReceiver extends BroadcastReceiver
    {
        protected DisplayActivity parent;
        protected ConnectionAdapter adapter;

        public AdapterReceiver(DisplayActivity parent, ConnectionAdapter toLoad)
        {
            this.parent = parent;
            adapter = toLoad;
        }
        
        @Override public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "received " + intent.getAction() + "; binding and updating");
            Intent service = new Intent(parent, SetupService.class);
            SetupService.SetupBinder binder = (SetupService.SetupBinder)peekService(parent, service);
            SetupService setup = binder.getService();
            adapter.setList(setup.connectionList());
            Log.d(TAG, "item count: " + adapter.getCount());
        }
    }

    protected ConnectionAdapter adapter;
    protected AdapterReceiver receiver;

    protected final ServiceConnection sConn = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            SetupService setup = ((SetupService.SetupBinder)service).getService();
            adapter.setList(setup.connectionList());
        }
        
        public void onServiceDisconnected(ComponentName className)
        {
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //construct the adapter, start the service, then
        //connect to the service to load the adapter with data.
        adapter = new ConnectionAdapter();
        setListAdapter(adapter);

        receiver = new AdapterReceiver(this, adapter);

        Log.d(TAG, "calling startservice for setup");
        startService(new Intent(this, SetupService.class));

    }

    /**
     * In this, set up a broadcast reciever to listen for
     * com.roundarch.statsapp.ACTION_UPDATE_COMPLETE, 
     * which be forwarded to the list adapter, in order to update the list, and 
     * com.roundarch.statsapp.ACTION_UPDATE
     * which will display a little updating animation until the _COMPLETE action
     * rebuild the list?
     */
    @Override public void onStart()
    {
        super.onStart();

        Log.d(TAG, "Registering receiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdaterService.ALL_UPDATES_COMPLETE);
        registerReceiver(receiver, filter);

        boolean bound = bindService(new Intent(this, SetupService.class), sConn, 0);
        if (bound) 
        {
            Log.d(TAG, "successfully bound service and updated adapter");
            unbindService(sConn);
        }
    }

    /**
     * might want to check that the list is up to date wrt setup
     */
    @Override public void onRestart()
    {
        super.onRestart();
        
    }

    /**
     * in this, disconnect the broadcast receiver
     */
    @Override public void onStop()
    {
        super.onStop();
        unregisterReceiver(receiver);

    }


    @Override public void onResume()
    {
        super.onResume();

    }

    @Override public void onPause()
    {
        super.onPause();

    }

    //use this to launch the settings activity or add connection activity or whatever.
    /*@Override public boolean onOptionsItemSelected(MenuItem item)
    {
        return true;
    }*/

}
