package com.roundarch.statsapp;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import android.widget.ListAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.database.DataSetObserver;
import android.content.Context;
import com.roundarch.statsapp.connections.TwitterConnection;
import static com.roundarch.statsapp.APIConnection.MockConnection;

public class ConnectionAdapter implements ListAdapter
{
    protected HashMap<String, Integer> typeIds;
    protected LinkedList<DataSetObserver> observers;
    protected List<APIConnection> conns;
    public ConnectionAdapter()
    {
        typeIds = new HashMap<String, Integer>(2);
        int id = 0;
        typeIds.put(MockConnection.type, id++);
        typeIds.put(TwitterConnection.type, id++);
        conns = new ArrayList<APIConnection>(0);
        observers = new LinkedList<DataSetObserver>();
    }

    public void setList(List<APIConnection> newList)
    {
        conns = newList;
        change();
    }

    public boolean areAllItemsEnabled()
    {
        return true;
    }

    public boolean isEnabled(int itemId)
    {
        return true;
    }

    public int getCount()
    {
        return conns.size();
    }

    public Object getItem(int position)
    {
        return conns.get(position);
    }

    public long getItemId(int position)
    {
        //wtf.
        return position;
    }

    public int getItemViewType(int position)
    {
        return typeIds.get(conns.get(position).getType());
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        APIConnection conn = conns.get(position);
        //if (convertView != null && conn.getId() == (Integer)convertView.getTag(R.id.kIDTag)) 
            //return convertView;
        //else
        return ConnViewGenerator.createView(parent.getContext(), conn);
    }

    public int getViewTypeCount()
    {
        //this number should be the number of implemented subtypes of
        //APIConnection that there are views for.
        return typeIds.size(); 
    }

    public boolean hasStableIds()
    {
        //not sure about this one.
        return false;
    }

    public boolean isEmpty()
    {
        return conns.isEmpty();
    }

    public void registerDataSetObserver(DataSetObserver observer)
    {
        observers.add(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        observers.remove(observer);
    }

    public void deleteItem(int position)
    {
        conns.remove(position);
        change();
    }

    protected void change()
    {
        for (DataSetObserver dso : observers)
        {
            dso.onChanged();
        }
    }

    protected void invalidate()
    {
        for (DataSetObserver dso : observers)
        {
            dso.onInvalidated();
        }
    }
}

