package com.roundarch.statsapp;
import com.roundarch.statsapp.connections.*;

import java.util.Map;
import java.util.HashMap;
import android.util.Log;

public abstract class APIConnection
{
    public static class MockConnection extends APIConnection
    {
        public static final String TAG = "com.roundarch.statsapp.APIConnection.MockConnection";
        public static final String kTESTING = "testing";
        public static final String kUpdateCount = "update_count";
        public static final String type = "MockConnection";
        
        protected String testVal;
        protected int updateCount;
        public MockConnection(HashMap<String, Object> cMap)
        {
            super(cMap);
            testVal = (String)cMap.get(kTESTING);
            if (cMap.containsKey(kUpdateCount))
                updateCount = (Integer)cMap.get(kUpdateCount);
            else
                setUpdateCount(0);
        }

        public String getTestVal()
        {
            return testVal;
        }

        public int getUpdateCount()
        {
            return updateCount;
        }

        protected void setUpdateCount(int count)
        {
            updateCount = count;
            fields.put(kUpdateCount, updateCount);
        }

        public void setTestVal(String newVal)
        {
            testVal = newVal;
            fields.put(kTESTING, testVal);
        }

        public void doUpdate()
        {
            //do nothing at all.
            Log.d(TAG, "update " + getUpdateCount() + " for " + type + " number " + getId());
            setUpdateCount(updateCount + 1);
        }

    }

    public static final String kTYPE = "type";
    public static final String kID = "id";

    public static APIConnection createConnection(HashMap<String, Object> cMap) //throws Exception
    {
        String type = (String)cMap.get(kTYPE);
        //...
        if (type.equals(TwitterConnection.type))
            return new TwitterConnection(cMap);
        else if (type.equals(MockConnection.type))
            return new MockConnection(cMap);
        else
            return null;
            //throw new Exception("Unrecognized type : " + type);
    }
    
    protected int id = -1;
    protected HashMap<String, Object> fields;

    public APIConnection(HashMap<String, Object> cMap)
    {
        fields = cMap;
        if (cMap.containsKey(kID))
            id = (Integer)cMap.get(kID);
    }

    public abstract void doUpdate();

    public int getId()
    {
        return id;
    }
    
    public void setId(int newId)
    {
        id = newId;
        fields.put(kID, newId);
    }

    public HashMap<String, Object> getFields()
    {
        return fields;
    }
}
