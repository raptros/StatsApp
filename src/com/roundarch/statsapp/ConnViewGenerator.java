package com.roundarch.statsapp;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Gravity;
import android.view.LayoutInflater;

import static com.roundarch.statsapp.APIConnection.MockConnection;
import com.roundarch.statsapp.connections.TwitterConnection;

public class ConnViewGenerator
{
    public static View createView(Context context, APIConnection conn)
    {
        View v = null;
        if (conn.getType().equals(MockConnection.type))
            v = createMockConnectionView(context, (MockConnection)conn);
        else if (conn.getType().equals(TwitterConnection.type))
            v = createTwitterConnectionView(context, (TwitterConnection)conn);
        if (v != null)
        {
            v.setTag(R.id.kIDTag, conn.getId());
            v.setTag(R.id.kTypeTag, conn.getType());
        }
        return v;
    }


    public static void updateView(Intent updated, View toUpdate)
    {
        Bundle extras = updated.getExtras();
        if (!extras.getString(APIConnection.kTYPE).equals((String)toUpdate.getTag(R.id.kTypeTag)))
            return ;
        if (extras.getString(APIConnection.kTYPE).equals(MockConnection.type))
            updateMockConnectionView(extras, (LinearLayout)toUpdate);
    }

    protected static View createMockConnectionView(Context context, MockConnection conn)
    {
        //load the ViewGroup from a layout resource.
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.mock_conn, null);
        TextView idVal = (TextView)root.findViewById(R.id.val_id);
        idVal.setText("" + conn.getId());
        TextView countVal = (TextView)root.findViewById(R.id.val_count);
        countVal.setText("" + conn.getUpdateCount());
        return root;
    }

    protected static void updateMockConnectionView(Bundle update, LinearLayout toUpdate)
    {
        TextView idLabel = (TextView)toUpdate.getChildAt(0);
        TextView countLabel = (TextView)toUpdate.getChildAt(1);
        countLabel.setText("count: " + update.get(MockConnection.kUpdateCount));
    }

    protected static View createTwitterConnectionView(Context context, TwitterConnection conn)
    {
        //load the ViewGroup from a layout resource.
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.twitter_conn, null);
        TextView username = (TextView)root.findViewById(R.id.username);
        username.setText(conn.getUser());
        TextView update = (TextView)root.findViewById(R.id.update);
        update.setText(conn.getLastUpdate());
        TextView followers = (TextView)root.findViewById(R.id.followers);
        followers.setText("" + conn.getFollowersCount());
        return root;
    }
}
