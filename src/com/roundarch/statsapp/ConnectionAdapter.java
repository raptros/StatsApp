package com.roundarch.statsapp;

import android.widget.ListAdapter;
import android.widget.View;

//TODO implement this thing!
public class ConnectionAdapter implements ListAdapter
{
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
        return 0;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public int getItemViewType(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }

    public int getViewTypeCount()
    {
        return 0;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isEmpty()
    {
        return true;
    }

    public void registerDataSetObserver(DataSetObserver observer)
    {
    }

    public void unregisterDataSetObserver(DataSetObserver observer)
    {
    }
}

