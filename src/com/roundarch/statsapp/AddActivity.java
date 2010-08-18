package com.roundarch.statsapp;

import java.util.HashMap;
import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;

import android.view.View;
import android.view.LayoutInflater;

import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Button;
import android.widget.AdapterView;

import com.roundarch.statsapp.connections.*;
import static com.roundarch.statsapp.APIConnection.MockConnection;

public class AddActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    protected Spinner typeChooser;
    protected SimpleAdapter typeAdapter;
    protected Button add, cancel;
    protected EditText twitterUser;
    protected String type;

    protected ArrayList<HashMap<String, String>> typeList;

    protected static final String kDisplayName = "display";
    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        //First, create the type adapter with the type map set up.
        typeList = new ArrayList<HashMap<String, String>>(2);
        HashMap<String, String> connType;
        //twitter user connection
        connType = new HashMap<String, String>(2);
        connType.put(APIConnection.kTYPE, TwitterConnection.type);
        connType.put(kDisplayName, "Twitter User");
        typeList.add(connType);
        //mock connection - for testing
        connType = new HashMap<String, String>(2);
        connType.put(APIConnection.kTYPE, MockConnection.type);
        connType.put(kDisplayName, "Mock Connection");
        typeList.add(connType);

        typeAdapter = new SimpleAdapter(this, typeList,
                R.layout.type_chooser_item,
                new String[] {kDisplayName},
                new int[] {R.id.type_name});

        //now get at the objects in the view
        typeChooser = (Spinner)findViewById(R.id.type_chooser);
        twitterUser = (EditText)findViewById(R.id.twitter_username);
        add = (Button)findViewById(R.id.add);
        cancel = (Button)findViewById(R.id.cancel);

        typeChooser.setAdapter(typeAdapter);
        
        //finally, set up the event handlers.
        typeChooser.setOnItemSelectedListener(this);
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override public void onStart()
    {
        super.onStart();
        setResult(RESULT_CANCELED);
        type=null;
        typeChooser.setSelection(0);
        twitterUser.setVisibility(View.VISIBLE);
    }

    public void onClick(View v)
    {
        if (v == add)
            doAdd();
        else if (v == cancel)
            doCancel();
    }
    
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        if (parent == typeChooser)
        {
            type = typeList.get(position).get(APIConnection.kTYPE);
            if (type.equals(TwitterConnection.type))
            {
                twitterUser.setVisibility(View.VISIBLE);
            }
            else if (type.equals(MockConnection.type))
            {
                twitterUser.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> parent)
    {
        type = null;
    }

    protected void doCancel()
    {
        finish();
    }

    protected void doAdd()
    {
        Intent data = new Intent();
        data.putExtra(APIConnection.kTYPE, type);
        if (type == null)
        {
            //what.
            finish();
        }
        else if (type.equals(TwitterConnection.type))
        {
            data.putExtra(TwitterConnection.kUser, twitterUser.getText().toString());
        }
        else if (type.equals(MockConnection.type))
        {
            data.putExtra(MockConnection.kTESTING, "AddActivity.doAdd");
        }
        setResult(RESULT_OK, data);
        //now, then.
        finish();
    }
    
}
