package com.roundarch.statsapp;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.os.SystemClock;
import android.content.Context;
import android.content.res.Resources;
import static com.roundarch.statsapp.APIConnection.MockConnection;
import static com.roundarch.statsapp.ConnStoreTest.makeMockCMap;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.roundarch.statsapp.DisplayActivityTest \
 * com.roundarch.statsapp.tests/android.test.InstrumentationTestRunner
 */
public class DisplayActivityTest extends ActivityInstrumentationTestCase2<DisplayActivity> {

    public DisplayActivityTest() {
        super("com.roundarch.statsapp", DisplayActivity.class);
    }

    @Override public void setUp() throws Exception
    {
        //service test case will setup service with those
        super.setUp(); 
        Context app = getInstrumentation().getTargetContext();
        Context sys = getInstrumentation().getContext();

        Resources res = app.getResources();
        String storeFile = res.getString(R.string.store_file);

        //delete store
        app.deleteFile(storeFile);
        ConnectionStore store = new ConnectionStore(app, storeFile);
        store.genConn(makeMockCMap(false, "item0", 0));
        store.genConn(makeMockCMap(false, "item1", 0));
        store.genConn(makeMockCMap(false, "item2", 0));
        store.save();

    }

    public void testShowList() throws Exception
    {
        DisplayActivity activity = getActivity();
        assertEquals(3, activity.getListView().getCount());
    }

    @Override public void tearDown() throws Exception
    {
        super.tearDown();
        Context app = getInstrumentation().getTargetContext();
        Context sys = getInstrumentation().getContext();

        Resources res = app.getResources();
        String storeFile = res.getString(R.string.store_file);

        //delete store
        app.deleteFile(storeFile);
    }

}
