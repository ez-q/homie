package viktor.at.homieapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class WSClient extends Observable {

    private static final String TAG = "at.viktor.WSClientTest";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private static WSClient INSTANCE = null;

    public static WSClient getInstance(){
        return INSTANCE == null ? INSTANCE = new WSClient() : INSTANCE;
    }

    private WSClient(){}


    public boolean start(final String wsuri) {

        Log.d(TAG, "attempt to start ws");

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    //HashMap<String,Object> map = new HashMap<String, Object>();
                    //JSONObject connectObject = new JSONObject(map);
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
                    Log.d(TAG, "Connection lost. "+reason);
                }
            });
            return true;
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
            return false;
        }
    }
}
