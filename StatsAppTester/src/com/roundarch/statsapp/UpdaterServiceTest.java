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

import static com.roundarch.statsapp.APIConnection.MockConnection;
import static com.roundarch.statsapp.ConnStoreTest.makeMockCMap;

public class UpdaterServiceTest extends ServiceTestCase
{
    //not sure why I put this here.
    public static class CompletionReportingConnection implements ServiceConnection
    {
        public boolean onServiceConnectedCompleted = false;
        public boolean onServiceDisconnectedCompleted = false;

        public void onServiceConnected(ComponentName name, IBinder service)
        {

            onServiceConnectedCompleted = true;
        }

        public void onServiceDisconnected(ComponentName name)
        {

            onServiceDisconnectedCompleted = true;
        }
    }

    public UpdaterServiceTest() {
        super(UpdaterService.class);
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

    public void testRunAnUpdate()
    {
        //startService(new Intent(sys, UpdaterService.class));
        sys.startService(new Intent(sys, SetupService.class));
        SystemClock.sleep(1000*2);
        sys.startService(new Intent(sys, UpdaterService.class));
        SystemClock.sleep(1000*2);
        sys.startService(new Intent(sys, UpdaterService.class));
        SystemClock.sleep(1000*2);
    }
}
