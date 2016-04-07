package viktor.at.homieapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/*
    Created By: Viktor Bär
    This class manages all the devices. It is both an Observable and an Observer so it can get data from the
    websocket class, process that data and notify the DeviceList and DeviceDetail activities.
 */
public class DeviceRepository extends Observable implements Observer {
    private static DeviceRepository INSTANCE = null;
    List<Device> deviceList;
    private static final String TAG = "at.viktor.WSClientTest";


    public static DeviceRepository getInstance(){
        return INSTANCE == null ? INSTANCE = new DeviceRepository() : INSTANCE;
    }

    private DeviceRepository(){
        deviceList = new LinkedList<>();
        WSClient.getInstance().addObserver(this);
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public void addDevice(Device d){
        deviceList.add(d);
    }

    public void addDeviceList(List<Device> deviceList){
        this.deviceList.addAll(deviceList);
    }

    public Device getDevice(String name){
        for(Device d : deviceList){
            if(d.getName().equals(name))
                return d;
        }
        return null;
    }

    /**
     * If the websocket client gets new data it will be forwarded here. This method checks for specific name occurrences in
     * the JSONObject and acts accordingly.
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        JSONObject message = (JSONObject)data;
        Log.d(TAG, "got message: " + message.toString());
        try {
            //If the JSONObject contains "devices" it means that the data sent is the current connected device list.
            //The devices are saved in a JSONArray. The current devices are replaced.
            if(message.has("devices")){
                List<Device> deviceList = new LinkedList<>();
                Log.d(TAG, "got devices list length = " + message.getJSONArray("devices").length());
                for(int i = 0; i < message.getJSONArray("devices").length();i++){
                    JSONObject object = message.getJSONArray("devices").getJSONObject(i);
                    if(!object.get("type").equals("time")) {
                        Device d = new Device();
                        d.setType(object.getString("type"));
                        d.setName(object.getString("dname"));
                        d.setValue(object.getString("latestValue"));
                        d.setCategory(object.getString("category"));
                        deviceList.add(d);
                        /*if(d.getType().equals("button")){
                            WSClient.getInstance().registerDevice(d.getName()+"Button","sensor","button");
                            WSClient.getInstance().createConfigForButton(d.getName());
                        }*/
                        Log.d(TAG, "Device added: " + d.getName());
                    }
                }
                setDeviceList(new LinkedList<>(deviceList));
                setChanged();
                notifyObservers("devices");
            } else if(message.has("data")){                                     //If the JSONObject contains "data" it means that the sent data is a new value for a device
                for(Device dev : getDeviceList()){
                    if(message.getString("dname").equals(dev.getName())){
                        switch(dev.getType()){
                            case "temperature":
                                dev.setValue(message.getDouble("data"));
                                Log.d(TAG, "Value " + message.getDouble("data") + " for device " + dev.getName());
                                break;
                            case "button":
                                dev.setValue(message.getBoolean("data"));
                                Log.d(TAG, "Value " + message.getBoolean("data") + " for device " + dev.getName());
                                break;
                        }
                        setChanged();
                        notifyObservers("data");
                        break;
                    }
                }
            } else if(message.has("historyData")){                              //If the JSONObject contains "historyData" it means that the sent data is the history for a device
                for(Device dev : getDeviceList()){                              // The date is saved in a JSONArray and contains the date in milliseconds from 1970 and the value (i.e. temperature)
                    if(message.getString("dname").equals(dev.getName())){
                        HashMap<Date, Object> map = new HashMap<>();
                        switch(dev.getType()){
                            case "temperature":
                                for (int i = 0; i < message.getJSONArray("historyData").length(); i++){
                                    map.put(new Date(Long.parseLong(message.getJSONArray("historyData").getJSONObject(i).getString("date"))),message.getJSONArray("historyData").getJSONObject(i).getDouble("value"));
                                }
                                Log.d(TAG, "Got historydata for device " + dev.getName());
                                break;
                            case "button":
                                for (int i = 0; i < message.getJSONArray("historyData").length(); i++){
                                    map.put(new Date(Long.parseLong(message.getJSONArray("historyData").getJSONObject(i).getString("date"))),message.getJSONArray("historyData").getJSONObject(i).getBoolean("value"));
                                }
                                Log.d(TAG, "Got historydata for device " + dev.getName());
                                break;
                        }
                        dev.setHistory(map);
                    }
                }
                notifyObservers("historyData");
            }
        } catch (JSONException e) {

        }
    }
}
