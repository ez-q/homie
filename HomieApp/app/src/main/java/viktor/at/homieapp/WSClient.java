package viktor.at.homieapp;

import android.util.Log;
import android.widget.Toast;

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
/*
    Created by Viktor Bär
    This is the websocket client itself. It manages the connection and the communication with the server.
 */

public class WSClient extends Observable {

    private static final String TAG = "at.viktor.WSClientTest";
    private final WebSocketConnection mConnection = new WebSocketConnection();
    private static WSClient INSTANCE = null;

    public WebSocketConnection getmConnection() {
        return mConnection;
    }

    //Singleton implementation
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
            //Trying to connection to the server with the specified URI
            mConnection.connect(wsuri, new WebSocketHandler() {

                /*
                    Here are 2 possible ways of starting.
                    1. Sending the getDevices JSON request so you get the list of all devices once.
                    2. Sending the subscribeLatestData JSON request to periodically get the currently connected devices.
                    Option 2 was chosen so the list is automatically refreshed.
                 */
                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    /*HashMap<String,Object> map = new HashMap<String, Object>();
                    map.put("event","regDevice");
                    map.put("category","sensor");
                    map.put("type","button");
                    map.put("name", "androidDevice");
                    //map.put("values","boolean");
                    JSONObject json = new JSONObject(map);
                    mConnection.sendTextMessage(json.toString());*/

                    /*HashMap<String,Object> deviceRequest = new HashMap<>();
                    deviceRequest.put("event","getDevices");
                    deviceRequest.put("data","");
                    JSONObject json = new JSONObject(deviceRequest);
                    mConnection.sendTextMessage(json.toString());*/

                    HashMap<String,Object> subscribe = new HashMap<>();
                    subscribe.put("event","subscribeLatestData");
                    subscribe.put("data","");
                    JSONObject json = new JSONObject(subscribe);
                    mConnection.sendTextMessage(json.toString());
                }
                /*
                    When the server sends a message, this method is called. It notifies the DeviceRepository and exchanges the data.
                 */
                @Override
                public void onTextMessage(String payload) {
                    try {
                        JSONObject message = new JSONObject(payload);
                        setChanged();
                        notifyObservers(message);
                    } catch (JSONException e) {
                        Log.d(TAG,payload);
                        Log.d(TAG,"not a JSON string");

                    }
                }

                /*
                    In case the connection is closed, it tries to disconnect from the server. This is only sometimes necessary as
                    the fields are not set correctly in rare occasions.
                 */
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

    /**
     * Sends a simple string to the server.
     * @param message
     */
    public void sendMessage(String message){
        if(mConnection.isConnected()) {
            mConnection.sendTextMessage(message);
        }
    }

    /**
     * Creates a JSON object out of the HashMap and sends it to the server.
     * @param map
     */
    public void sendMessage(HashMap<String,Object> map){
        if(mConnection.isConnected()) {
            mConnection.sendTextMessage(new JSONObject(map).toString());
        }
    }

    /**
     * Sends the forceDeviceSendData JSON command to the server with the specified device name.
     * @param deviceName
     */
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

    /**
     * Sends the forceDeviceToExecuteCommand JSON command to the server with the specified device name and the desired action.
     * @param dName
     * @param action
     */
    public void forceDeviceToExecuteCommand(String dName,boolean action){
        if(mConnection.isConnected()){
            if(DeviceRepository.getInstance().getDevice(dName).getCategory().equals("actor")) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("event", "forceDeviceToExecuteCommand");
                HashMap<String, Object> innerMap = new HashMap<>();
                innerMap.put("dname", dName);
                String actString = action ? "on" : "off";
                innerMap.put("action", actString);
                JSONObject inner = new JSONObject(innerMap);
                map.put("data", inner);
                JSONObject object = new JSONObject(map);

                Log.d(TAG,"send json "+object.toString());
                mConnection.sendTextMessage(object.toString());
            }
        }
    }

    /**
     * Sends the setDataType JSON command to the server with the specified sevice name.
     * @param dName
     */
    public void setDataType(String dName){
        if(mConnection.isConnected()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("event", "setDevice");
            map.put("data", dName);
            JSONObject object = new JSONObject(map);
            mConnection.sendTextMessage(object.toString());
        }
    }

    /**
     * Dynamically registers the device that was passed in the parameters on the server.
     * @param dname
     * @param category
     * @param type
     *//*
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
    }*/

    /**
     * Dynamically creates the configurations for the button to create a connection between the button and the actor.
     * @param dname
     *//*
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
    }*/

}
