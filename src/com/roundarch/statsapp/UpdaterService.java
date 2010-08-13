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
            for (APIConnection conn : connList)
            {
                //update the connection
                conn.doUpdate();
                //once it is updated, broadcast an intent with the fields.
                Intent connUpdated = getIntentForConnection(conn);
                parent.sendBroadcast(connUpdated); //something like this.
            }
            //finished.
        }
        public void onServiceDisconnected(ComponentName className)
        {
            setup = null;
        }

        public static Intent getIntentForConnection(APIConnection conn)
        {
            Intent updated = new Intent("com.roundarch.statsapp.ACTION_UPDATE_COMPLETE");
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
        Log.d(TAG, "update intent received");
        //bind SetupService to ask it for the api conns.
        boolean bound = bindService(new Intent(this, SetupService.class), sConn, BIND_AUTO_CREATE);
        if (!bound)
            Log.d(TAG, "failed to bind service");
        else
        {
            Log.d(TAG, "update complete, unbinding");
            unbindService(sConn);
        }
    }


}
