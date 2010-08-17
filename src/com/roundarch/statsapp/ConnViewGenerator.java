package com.roundarch.statsapp;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        LinearLayout lin = new LinearLayout(context);
        TextView idLabel = new TextView(context);
        idLabel.setText("id: " + conn.getId());
        lin.addView(idLabel, 0);
        TextView countLabel = new TextView(context);
        countLabel.setText("count: " + conn.getUpdateCount());
        lin.addView(countLabel, 1);
        return lin;
    }

    protected static void updateMockConnectionView(Bundle update, LinearLayout toUpdate)
    {
        TextView idLabel = (TextView)toUpdate.getChildAt(0);
        TextView countLabel = (TextView)toUpdate.getChildAt(1);
        countLabel.setText("count: " + update.get(MockConnection.kUpdateCount));
    }

    protected static View createTwitterConnectionView(Context context, TwitterConnection conn)
    {
        LinearLayout lin = new LinearLayout(context);
        TextView countLabel = new TextView(context);
        return lin;
    }
}
