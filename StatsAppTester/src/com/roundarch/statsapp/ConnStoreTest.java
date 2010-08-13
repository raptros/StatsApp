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

public class ConnStoreTest extends AndroidTestCase
{
    public static final String TEST_FILE="test_file";

    /*public class FileRefingContext extends MockContext
    {

    }*/

    protected ConnectionStore store;

    protected String getStringForException(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.print(" ");
        pw.print(e.getLocalizedMessage());
        pw.println();
        e.printStackTrace(pw);
        return sw.toString();
    }

    public void testMockConnection()
    {
        HashMap<String, Object> cMap = new HashMap<String,Object>(2);
        cMap.put(APIConnection.kTYPE, APIConnection.MockConnection.type);
        cMap.put(APIConnection.kID, 10);
        cMap.put(APIConnection.MockConnection.kTESTING, "blarg");
        APIConnection mock = APIConnection.createConnection(cMap);
        assertEquals(mock.getId(), 10);
        assertEquals(((APIConnection.MockConnection)mock).getTestVal(), "blarg");
        assertAssignableFrom(APIConnection.MockConnection.class, mock);

    }

    public static HashMap<String, Object> makeMockCMap(boolean useID, String testString, int id)
    {
        HashMap<String, Object> cMap = new HashMap<String,Object>(2);
        cMap.put(APIConnection.kTYPE, MockConnection.type);
        if (useID)
            cMap.put(APIConnection.kID, id);
        cMap.put(MockConnection.kTESTING, testString);
        return cMap;
    }

    protected MockConnection makeMockConnection(boolean useID, String testString, int id)
    {
        APIConnection mock = APIConnection.createConnection(makeMockCMap(useID, testString, id));
        return (MockConnection)mock;
    }

    public void testCreation()
    {
        try
        {
            store = new ConnectionStore(mContext, TEST_FILE);
        }
        catch (Exception e)
        {
            fail("store construction failed because" + getStringForException(e));
        }
    }

    public void testAddingToEmptyStore()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock = null;
        try {
            mock = makeMockConnection(false, "toEmpty", 0);     
        } catch (Exception e) {
            fail("failed to make mock connection" + getStringForException(e));
        }
        store.add(mock);
        assertEquals(0, mock.getId());
    }

    public void testGettingFromStore()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock = makeMockConnection(false, "toEmpty", 0);     
        store.add(mock);
        assertEquals(0, mock.getId());
        assertNotNull(store.get(0));
        assertSame(mock, store.get(0));
    }

    public void testAddingTwoToEmptyStore()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = makeMockConnection(false, "toEmpty1", 0);     
        APIConnection mock2 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock1);
        store.add(mock2);
        assertEquals(0, mock1.getId());
        assertEquals(1, mock2.getId());
        assertEquals(2, store.size());
    }
    
    public void testAddingAndDeletingOne()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock = makeMockConnection(false, "toEmpty", 0);     
        store.add(mock);
        assertEquals(0, mock.getId());
        assertSame(mock, store.delete(0));
        assertEquals(0, store.size());
    }

    public void testAddIndexeCorrectAfterAdd3DeleteLast()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = makeMockConnection(false, "toEmpty1", 0);     
        APIConnection mock2 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock3 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock4 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock1);
        store.add(mock2);
        store.add(mock3);
        store.delete(mock3.getId());
        store.add(mock4);
        assertEquals(2, mock4.getId());
        APIConnection mock5 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock5);
        assertEquals(3, mock5.getId());
    }

    public void testAddIndexCorrectAfterAdd3DeleteFirst()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = makeMockConnection(false, "toEmpty1", 0);     
        APIConnection mock2 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock3 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock4 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock1);
        store.add(mock2);
        store.add(mock3);
        store.delete(mock1.getId());
        store.add(mock4);
        assertEquals(0, mock4.getId());
        APIConnection mock5 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock5);
        assertEquals(3, mock5.getId());
    }

    public void testAddIndexCorrectAfterAdd3DeleteMiddle()
    {
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = makeMockConnection(false, "toEmpty1", 0);     
        APIConnection mock2 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock3 = makeMockConnection(false, "toEmpty2", 0);     
        APIConnection mock4 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock1);
        store.add(mock2);
        store.add(mock3);
        store.delete(mock2.getId());
        store.add(mock4);
        assertEquals(1, mock4.getId());
        APIConnection mock5 = makeMockConnection(false, "toEmpty2", 0);     
        store.add(mock5);
        assertEquals(3, mock5.getId());
    }


    public void testSaveCreatesFile()
    {
        mContext.deleteFile(TEST_FILE);
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = store.genConn(makeMockCMap(false, "createFile", 0));
        APIConnection mock2 = store.genConn(makeMockCMap(false, "createFile", 0));
        APIConnection mock3 = store.genConn(makeMockCMap(false, "createFile", 0));
        boolean result = store.save();
        assertTrue(result);
        File written = mContext.getFileStreamPath(TEST_FILE);
        assertTrue(written.exists());
        assertTrue(written.length() > 0);


    }

    public void testSaveThenLoad() throws Exception
    {
        mContext.deleteFile(TEST_FILE);
        store = new ConnectionStore(mContext, TEST_FILE);
        APIConnection mock1 = store.genConn(makeMockCMap(false, "createFile", 0));
        APIConnection mock2 = store.genConn(makeMockCMap(false, "createFile", 0));
        APIConnection mock3 = store.genConn(makeMockCMap(false, "createFile", 0));
        boolean result = store.save();
        assertTrue(result);
        store = new ConnectionStore(mContext, TEST_FILE);
        result = store.load();
        assertEquals(3, store.size());
        assertTrue(result);
    }


    public void testIndexesProperAfterSaveAndLoad()
    {
        mContext.deleteFile(TEST_FILE);
        store = new ConnectionStore(mContext, TEST_FILE);
        store.genConn(makeMockCMap(true, "createFile", 1));
        store.genConn(makeMockCMap(true, "createFile", 4));
        store.genConn(makeMockCMap(true, "createFile", 5));
        boolean result = store.save();
        assertTrue(result);
        store = new ConnectionStore(mContext, TEST_FILE);
        result = store.load();
        assertEquals(3, store.size());
        assertTrue(result);
        APIConnection mock;
        mock = store.genConn(makeMockCMap(false, "createFile", 0));
        assertEquals(0, mock.getId());
        mock = store.genConn(makeMockCMap(false, "createFile", 0));
        assertEquals(2, mock.getId());
        mock = store.genConn(makeMockCMap(false, "createFile", 0));
        assertEquals(3, mock.getId());
        mock = store.genConn(makeMockCMap(false, "createFile", 0));
        assertEquals(6, mock.getId());
        mock = store.genConn(makeMockCMap(false, "createFile", 0));
        assertEquals(7, mock.getId());
    }

}
