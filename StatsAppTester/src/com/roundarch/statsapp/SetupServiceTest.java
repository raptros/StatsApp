package com.roundarch.statsapp;

import android.test.ServiceTestCase;
import android.test.mock.MockContext;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.IBinder;
import android.content.Context;
import android.content.ContentResolver;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import static com.roundarch.statsapp.APIConnection.MockConnection;
import static com.roundarch.statsapp.ConnStoreTest.makeMockCMap;

import android.content.pm.ApplicationInfo;

public class SetupServiceTest extends ServiceTestCase
{

    public static class SetupTestMockContext extends MockContext
    {
        protected Context real;

        public SetupTestMockContext(Context realContext)
        {
            real = realContext;
        }

        @Override public String getPackageName()
        {
            return real.getPackageName();
        }

        @Override public ApplicationInfo getApplicationInfo()
        {
            return real.getApplicationInfo();
        }

        @Override public Resources getResources()
        {
            return real.getResources();
        }

        @Override public Object getSystemService(String name)
        {
            if (name == ALARM_SERVICE)
                return null;
            else
                return super.getSystemService(name);
        }

        @Override public SharedPreferences getSharedPreferences(String name, int mode)
        {
            return real.getSharedPreferences(name, mode);
        }
        @Override public  ContentResolver getContentResolver()
        {
            return real.getContentResolver();
        }

        @Override public boolean deleteFile(String name)
        {
            return real.deleteFile(name);
        }

        @Override public FileInputStream openFileInput(String name) throws FileNotFoundException
        {
            return real.openFileInput(name);
        }
        @Override public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException
        {
            return real.openFileOutput(name, mode);
        }
    }

    public SetupServiceTest() {
        super(SetupService.class);
    }
    
    protected String storeFile; 
    
    protected Context sys, mock;
   
    @Override public void setUp() throws Exception
    {
        //service test case will setup service with those
        super.setUp(); 
        sys = getSystemContext();

        Resources res = sys.getResources();
        storeFile = res.getString(R.string.store_file);

        //set up mock context and mock application
        mock = new SetupTestMockContext(sys);

        setContext(mock);
        //delete store
        sys.deleteFile(storeFile);
    }

    @Override public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /*protected HashMap<String, Object> makeMockCMap(boolean useID, String testString, int id)
    {
        HashMap<String, Object> cMap = new HashMap<String,Object>(2);
        cMap.put(APIConnection.kTYPE, MockConnection.type);
        if (useID)
            cMap.put(APIConnection.kID, id);
        cMap.put(MockConnection.kTESTING, testString);
        return cMap;
    }*/

    protected void createTestStore()
    {
        sys.deleteFile(storeFile);
        ConnectionStore store = new ConnectionStore(sys, storeFile);
        store.genConn(makeMockCMap(false, "item0", 0));
        store.genConn(makeMockCMap(false, "item1", 0));
        store.genConn(makeMockCMap(false, "item2", 0));
        store.save();
    }

    public void testServiceHasStoredConns()
    {
        createTestStore();
        SetupService boundService = ((SetupService.SetupBinder)bindService(new Intent(mock, SetupService.class))).getService();
        List<APIConnection> conns = boundService.connectionList();
        assertEquals(3, conns.size());
    }

    public void testAddingConnectionToServiceWithEmptyStore()
    {
        SetupService boundService = ((SetupService.SetupBinder)bindService(new Intent(mock, SetupService.class))).getService();
        List<APIConnection> conns = boundService.connectionList();
        assertEquals(0, conns.size());

        APIConnection conn = boundService.createConnection(makeMockCMap(false, "item0", 0));
        conns = boundService.connectionList();
        assertEquals(1, conns.size());
        assertEquals(0, conn.getId());
        assertEquals(0, boundService.getConnection(0).getId());

    }

    //public void testSetup
}
