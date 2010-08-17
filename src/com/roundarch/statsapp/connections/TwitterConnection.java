package com.roundarch.statsapp.connections;
import com.roundarch.statsapp.APIConnection;
import java.util.HashMap;
import java.util.Locale;
import org.json.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class TwitterConnection extends APIConnection
{
    public static final String type="TwitterConnection";
    public static final String kUser = "user";
    public static final String kLastUpdate = "last_update";
    public static final String kFollowersCount = "followers_count";
    public static final int VERSION = 1;
    public static final String APIURL = "http://api.twitter.com/" + VERSION +"/users/show.json?screen_name=%s";
    public static final int HTTP_STATUS_OK = 200;

    public static final String userAgent = "com.roundarch.statsapp";
    public static synchronized String urlGet(String url) throws IOException
    {
        // Create client and set our specific user-agent string
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", userAgent);

        HttpResponse response = client.execute(request);

        // Check if server response is valid
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != HTTP_STATUS_OK)
        {
            throw new IOException("Invalid response from server: " + status.toString());
        }

        // Pull content stream from response
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        ByteArrayOutputStream content = new ByteArrayOutputStream();

        byte[] sBuffer = new byte[512];

        // Read response into a buffered stream
        int readBytes = 0;
        while ((readBytes = inputStream.read(sBuffer)) != -1)
        {
            content.write(sBuffer, 0, readBytes);
        }

        // Return result from buffered stream
        return new String(content.toByteArray());
    }

    protected String user;
    protected String lastUpdate;
    protected int followersCount; //, updateCount, friendCount;

    public TwitterConnection(HashMap<String, Object> cMap)
    {
        super(cMap);
        if (cMap.containsKey(kUser))
            user = (String)cMap.get(kUser);
        if (cMap.containsKey(kLastUpdate))
            lastUpdate = (String)cMap.get(kLastUpdate);
        if (cMap.containsKey(kFollowersCount))
            followersCount = (Integer)cMap.get(kFollowersCount);
    }

    public String getUser()
    {
        return user;
    }

    public String getLastUpdate()
    {
        return lastUpdate;
    }
    
    public int getFollowersCount()
    {
        return followersCount;
    }

    protected void setFollowersCount(int newCount)
    {
        followersCount = newCount;
        fields.put(kFollowersCount, followersCount);
    }

    protected void setLastUpdate(String newUpdate)
    {
        lastUpdate = newUpdate;
        fields.put(kLastUpdate, followersCount);
    }

    public String getData() throws IOException
    {
        String url = String.format(Locale.US, APIURL, user);
        return urlGet(url);
    }

    public boolean doUpdate()
    {
        if (user == null)
            return false;

        try
        {
            String jsonData = getData();
            JSONObject userInfo = new JSONObject(jsonData);
            setFollowersCount(userInfo.getInt("followers_count"));
            setLastUpdate(userInfo.getJSONObject("status").getString("text"));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
        //return false;
    }


    public String getType()
    {
        return type;
    }
}
