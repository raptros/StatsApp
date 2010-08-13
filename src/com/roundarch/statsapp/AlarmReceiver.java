package com.roundarch.statsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.res.Resources;

public class AlarmReceiver extends BroadcastReceiver
{
    public static final String TAG="com.roundarch.statsapp.AlarmReciever";
    @Override public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "received intent, launching UpdaterService");
        Resources res = context.getResources();
        String action = res.getString(R.string.launch_updater_service);
        context.startService(new Intent(action));
    }
}
