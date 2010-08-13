package com.roundarch.statsapp.connections;
import com.roundarch.statsapp.APIConnection;
import java.util.HashMap;

public class TwitterConnection extends APIConnection
{
    public static final String type="TwitterConnection";

    public TwitterConnection(HashMap<String, Object> cMap)
    {
        super(cMap);
    }

    public void doUpdate()
    {

    }

}
