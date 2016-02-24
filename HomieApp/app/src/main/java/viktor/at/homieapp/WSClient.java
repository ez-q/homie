package viktor.at.homieapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    public void sendMessage(HashMap<String,Object> map){
        if(mConnection.isConnected()) {
            mConnection.sendTextMessage(new JSONObject(map).toString());
        }
    }

    public void forceDeviceSendData(String deviceName){
        if(mConnection.isConnected()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("event", "forceDeviceToSendData");
            HashMap<String, String> innerMap = new HashMap<>();
            innerMap.put("dname", deviceName);
            map.put("data", innerMap);
            JSONObject object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());
        }
    }

    public void registerDevice(String dname, String category, String type){
        if(mConnection.isConnected()){
            HashMap<String,String> map = new HashMap<>();
            map.put("event","regDevice");
            map.put("category", category);
            map.put("type",type);
            map.put("dname",dname);
            JSONObject object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());
        }
    }

    public void createConfigForButton(String dname){
        if(mConnection.isConnected()){
            HashMap<String,Object> map = new HashMap<>();
            HashMap<String,Object> dataMap = new HashMap<>();
            HashMap<String, Object> conditionMap = new HashMap<>();

            dataMap.put("cname", dname + "ButtonOffCondition");
            map.put("event", "deleteConfiguration");
            map.put("data", dataMap);
            JSONObject object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());
            dataMap.put("cname", dname + "ButtonOnCondition");
            map.put("event", "deleteConfiguration");
            map.put("data",dataMap);
            object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());

            conditionMap = new HashMap<String, Object>();
            conditionMap.put("dname", dname + "Button");
            conditionMap.put("value", true);
            conditionMap.put("mod", null);

            dataMap = new HashMap<>();
            dataMap.put("cname", dname + "ButtonOnCondition");
            dataMap.put("dname",dname);
            dataMap.put("action","on");
            List<HashMap<String,Object>> tempList = new LinkedList<>();
            tempList.add(conditionMap);
            dataMap.put("conditions", tempList);

            map = new HashMap<>();
            map.put("event","newConfiguration");
            map.put("data", dataMap);
            object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());

            dataMap.put("cname", dname + "ButtonOffCondition");
            dataMap.put("action", "off");
            conditionMap.put("value", false);
            object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());
        }
    }

}
