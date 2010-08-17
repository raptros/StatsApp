package com.roundarch.statsapp;

import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import static android.test.MoreAsserts.*;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import static com.roundarch.statsapp.APIConnection.MockConnection;

import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;
import com.roundarch.statsapp.connections.TwitterConnection;

/*
 * To use this test in an emulator, you need to let it get to the internet.
 */
public class TwitterConnTest extends AndroidTestCase
{
    protected TwitterConnection conn;

    @Override public void setUp()
    {
        HashMap<String, Object> cMap = new HashMap<String,Object>(2);
        cMap.put(APIConnection.kTYPE, TwitterConnection.type);
        cMap.put(TwitterConnection.kUser, "raptros_");

        conn = (TwitterConnection)APIConnection.createConnection(cMap);
    }

    public void testGetData() throws IOException
    {
        
        String data = conn.getData();
        assertNotNull(data);
        assertTrue(data.length() > 0);
    }

    public void testGetUpdate()
    {
        assertTrue("update failed", conn.doUpdate());
        assertTrue("bad followers count", conn.getFollowersCount() > 0);
    }
}
