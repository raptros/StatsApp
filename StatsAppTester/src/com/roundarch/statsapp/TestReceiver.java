package com.roundarch.statsapp;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Bundle;

public class TestReceiver extends BroadcastReceiver
{
    public static final String TAG="com.roundarch.statsapp.TestReceiver";
    @Override public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(UpdaterService.UPDATE_COMPLETE))
            Log.d(TAG, "received update complete intent " + intent.getAction() + " " + describeExtras(intent));
        else if (intent.getAction().equals(UpdaterService.ALL_UPDATES_COMPLETE))
            Log.d(TAG, "received all updates complete" + intent.getAction());


    }

    public static String describeExtras(Intent intent)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Bundle extras = intent.getExtras();
        Object v;
        for (String k : extras.keySet())
        {
            sb.append(" ");
            sb.append(k);
            sb.append(":");
            sb.append(extras.get(k));
        }
        sb.append(" ");
        sb.append("}");
        return sb.toString();
    }
}
