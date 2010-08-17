package com.roundarch.statsapp;

import java.util.List;
import java.util.HashMap;
import android.app.IntentService;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.res.Resources;

public class UpdaterService extends IntentService
{
    public static final String ALL_UPDATES_COMPLETE = "com.roundarch.statsapp.ACTION_ALL_UPDATES_COMPLETE";
    public static final String UPDATE_COMPLETE = "com.roundarch.statsapp.ACTION_UPDATE_COMPLETE";
    public static final String kSucceses = "successes";
    public static final String kFailures = "failures";
    public static class SetupConnector implements ServiceConnection
    {
        UpdaterService parent;
        public SetupConnector(UpdaterService parent)
        {
            this.parent = parent;
        }
        private SetupService setup;
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            setup = ((SetupService.SetupBinder)service).getService();
            //now that the service is bound as setup, get to work.
            //get the list of apiconnections, and loop over it
            List<APIConnection> connList = setup.connectionList();
            int succeeds = 0, fails = 0;
            for (APIConnection conn : connList)
            {
                //update the connection
                if (conn.doUpdate())
                {
                    succeeds++;
                    //once it is updated, broadcast an intent with the fields.
                    Intent connUpdated = getIntentForConnection(conn);
                    parent.sendBroadcast(connUpdated); //something like this.
                }
                else
                    fails++;
            }
            //finished.
            Intent finished = new Intent(ALL_UPDATES_COMPLETE);
            //put in some sucess/fail numbers?
            finished.putExtra(kSucceses, succeeds);
            finished.putExtra(kFailures, fails);
            parent.sendBroadcast(finished);
        }
        public void onServiceDisconnected(ComponentName className)
        {
            setup = null;
        }

        public static Intent getIntentForConnection(APIConnection conn)
        {
            Intent updated = new Intent(UPDATE_COMPLETE);
            HashMap<String, Object> fields = conn.getFields();
            Object v;
            for (String k : fields.keySet())
            {
                v = fields.get(k);
                if (v instanceof Integer)
                    updated.putExtra(k, (Integer)v);
                else if (v instanceof String)
                    updated.putExtra(k, (String)v);
            }
            return updated;
        }
    }

    public static final String TAG="com.roundarch.statsapp.UpdaterService";
    
    protected SetupConnector sConn;
    public UpdaterService()
    {
        super(TAG);
        sConn = new SetupConnector(this);
    }

    @Override public void onHandleIntent(Intent intent)
    {
        //should probably foreground this service at this point, so there's
        //a notification that it is updating. TODO.
        Log.d(TAG, "update intent received");
        //bind SetupService to ask it for the api conns.
        boolean bound = bindService(new Intent(this, SetupService.class), sConn, 0);
        if (!bound)
            Log.d(TAG, "failed to bind service");
        else
        {
            Log.d(TAG, "update complete, unbinding");
            unbindService(sConn);
        }
    }


}
