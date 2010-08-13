package com.roundarch.statsapp;

import android.test.ServiceTestCase;
import android.test.mock.MockContext;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import java.util.List;

import static com.roundarch.statsapp.APIConnection.MockConnection;
import static com.roundarch.statsapp.ConnStoreTest.makeMockCMap;

public class UpdaterServiceTest extends ServiceTestCase
{
    public static final String TAG = "com.roundarch.statsapp.UpdaterServiceTest";
    public UpdaterServiceTest() {
        /* now, you might be asking yourself, why does this test use setupservice?
         * well, actually, it doesn't really.  the issue is that updaterservice 
         * relies on setupservice running. remember that updaterservice has only 
         * one function, and only one way of being interacted with.
         * setupservice is what causes updaterservice be run, and the effects of 
         * updaterservice are on things accesed through setupservice. thus, to 
         * test updaterservice, I have to run setupservice, defining the environemnt
         * that the updaterservice will run in, and use setupservice to test the work
         * of updaterservice. 
         */
        super(SetupService.class);
    }
    
    protected Context sys;
   
    @Override public void setUp() throws Exception
    {
        //service test case will setup service with those
        super.setUp(); 
        sys = getSystemContext();

        Resources res = sys.getResources();
        String storeFile = res.getString(R.string.store_file);

        //delete store
        sys.deleteFile(storeFile);
        ConnectionStore store = new ConnectionStore(sys, storeFile);
        store.genConn(makeMockCMap(false, "item0", 0));
        store.genConn(makeMockCMap(false, "item1", 0));
        store.genConn(makeMockCMap(false, "item2", 0));
        store.save();

    }
    protected SetupService setup;

    public void testRunAnUpdate()
    {
        List<APIConnection> conns;
        MockConnection mock;
        ServiceConnection mServiceConn = new ServiceConnection()
        {
            public void onServiceConnected(ComponentName className, IBinder service)
            {
                setup = ((SetupService.SetupBinder)service).getService();
            }

            public void onServiceDisconnected(ComponentName className)
            {
                setup = null;
            }
        };

        //this resembles the way that setupservice should be run.
        //start service, then bind to it only after it's been started.
        sys.startService(new Intent(sys, SetupService.class));
        boolean bound = sys.bindService(new Intent(sys, SetupService.class), mServiceConn, sys.BIND_AUTO_CREATE);
        assertTrue("could not bind to service", bound);
        for (int count = 1; count < 4; count++)
        {
            SystemClock.sleep(1000*3);
            conns = setup.connectionList();
            for (APIConnection conn : conns)
            {
                mock = (MockConnection)conn;
                String pt = "count " + count + " id " + mock.getId() + " name " + mock.getTestVal();
                Log.d(TAG, pt);
                assertEquals(pt, count, mock.getUpdateCount());
            }
            if (count < 3)
                sys.startService(new Intent(sys, UpdaterService.class));
        }
        sys.unbindService(mServiceConn);
    }
}
