package com.roundarch.statsapp;

import android.content.Context;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collection;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConnectionStore
{
    protected String file;
    protected Context mContext;

    protected HashMap<Integer, APIConnection> index;
    protected TreeSet<Integer> available;
    protected int nextID = 0;

    public ConnectionStore(Context context, String loadFile)
    {
        file = loadFile;
        mContext = context;
        index = new HashMap<Integer, APIConnection>();
        available = new TreeSet<Integer>();
    }

    /**
     * When loading, any connections in the map already
     * could get destroyed
     */
    public boolean load()// throws Exception
    {
        //reads in HashMaps from file, builds the available set, and 
        //generates api connections.
        try
        {
            FileInputStream fIn = mContext.openFileInput(file);
            ObjectInputStream deserialize = new ObjectInputStream(fIn);
            //how many connections were stored
            int count = deserialize.readInt();
            HashMap<String, Object>[] outArr = new HashMap[count];
            int currID; 
            for (int i = 0; i < count; i++)
            {
                //get a hashmap out of the file
                outArr[i] = (HashMap<String, Object>)deserialize.readObject();
                //determine if the ID in this map is greater than what's been seen,
                //and hang onto it if it is
                currID = (Integer)outArr[i].get(APIConnection.kID);
                nextID = (currID > nextID) ? currID : nextID;
            }
            deserialize.close();
            nextID++;
            //add every number between 0 and the highest ID we saw inclusive
            for (int i = 0; i < nextID; i++)
            {
                available.add(i);
            }
            //running genConn over every map will create the APIConnections,
            //add them to the map, and remove their IDs from the available set.
            for (HashMap<String, Object> current : outArr)
            {
                genConn(current);
            }
        }
        catch (Exception e)
        {
            //throw e;
            //not very informative I know.
            return false;
        }
        return true;

    }

    public boolean save()
    {
        try
        {
            //what this does is write the fields HashMap of each APIConnection in the 
            //index to file.
            FileOutputStream fOut = mContext.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream serialize = new ObjectOutputStream(fOut);
            serialize.writeInt(index.size());
            HashMap<String, Object> curr;
            for (APIConnection conn : index.values())
            {
                curr = conn.getFields();
                serialize.writeObject(curr);
            }
            serialize.flush();
            serialize.close();
        }
        catch (Exception e)
        {
            //not very informative I know.
            return false;
        }
        return true;
    }

    public APIConnection get(int id)
    {
        return index.get(id);
    }

    public APIConnection add(APIConnection conn)
    {
        //if it already has an id, remove that from available.
        if (conn.getId() >= 0)
            available.remove(conn.getId());
        else
        {
            if (available.isEmpty())
            {
                conn.setId(nextID);
                nextID++;
            }
            else
            {
                conn.setId(available.first());
                available.remove(available.first());
            }
        }

        index.put(conn.getId(), conn);
        return conn;
    }

    public int size()
    {
        return index.size();
    }

    public APIConnection delete(int id)
    {
        APIConnection deleted = index.remove(id);
        if (id == nextID - 1)
            nextID--;
        else
            available.add(id);
        return deleted;
    }

    public APIConnection genConn(HashMap<String, Object> cMap)
    {
        APIConnection conn = APIConnection.createConnection(cMap);
        add(conn);
        return conn;
    }

    public Set<Integer> idSet()
    {
        return index.keySet();
    }

}
