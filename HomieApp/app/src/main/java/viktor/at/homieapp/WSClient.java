package viktor.at.homieapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Observable;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class WSClient extends Observable {

    private static final String TAG = "at.viktor.WSClientTest";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private static WSClient INSTANCE = null;

    public WebSocketConnection getmConnection() {
        return mConnection;
    }

    public static WSClient getInstance(){
        if(INSTANCE == null){
            INSTANCE = new WSClient();
        }
        return INSTANCE;
    }

    private WSClient(){}


    public boolean start(final String wsuri) {

        Log.d(TAG, "attempt to start ws");

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {


                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put("event","regDevice");
                    map.put("category","sensor");
                    map.put("type","button");
                    map.put("name","androidDevice");
                    //map.put("values","boolean");
                    JSONObject connectObject = new JSONObject(map);
                    mConnection.sendTextMessage(connectObject.toString());
                }

                @Override
                public void onTextMessage(String payload) {
                    try {
                        JSONObject message = new JSONObject(payload);
                        setChanged();
                        notifyObservers(message);

                    } catch (JSONException e) {
                        Log.d(TAG,"not a JSON string");
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    if(mConnection.isConnected())
                        mConnection.disconnect();
                    Log.d(TAG, "Connection lost. "+reason);
                }
            });
            return true;
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
            return false;
        }
    }

    public void sendMessage(String message){
        if(mConnection.isConnected()) {
            mConnection.sendTextMessage(message);
        }
    }


}
