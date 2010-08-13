package com.roundarch.statsapp;

import android.app.Activity;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.os.Bundle;


public class DisplayActivity extends ListActivity
{
    //TODO options menu ... 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //here, ensure that SetupService is running.
        //then, construct the view adapter.
        //initially, throw api connection information at it.
        //
    }

    /**
     * In this, set up a broadcast reciever to listen for
     * com.roundarch.statsapp.ACTION_UPDATE_COMPLETE, 
     * which be forwarded to the list adapter, in order to update the list, and 
     * com.roundarch.statsapp.ACTION_UPDATE
     * which will display a little updating animation until the _COMPLETE action
     * rebuild the list?
     */
    @Override public void onStart()
    {

    }

    /**
     * might want to check that the list is up to date wrt setup
     */
    @Override public void onRestart()
    {
        
    }

    /**
     * in this, disconnect the broadcast receivers that are listening for
     * com.roundarch.statsapp.ACTION_UPDATE_COMPLETE and
     * com.roundarch.statsapp.ACTION_UPDATE
     */
    @Override public void onStop()
    {

    }


    @Override public void onResume()
    {

    }

    /**
     * save whatever needs to be saved, if anything.
     */
    @Override public void onPause()
    {

    }

    //use this to launch the settings activity or add connection activity or whatever.
    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        return true;
    }

}
