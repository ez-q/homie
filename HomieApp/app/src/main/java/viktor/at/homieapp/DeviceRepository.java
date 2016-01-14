package viktor.at.homieapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


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

    @Override
    public void update(Observable observable, Object data) {
        JSONObject message = (JSONObject)data;
        Log.d(TAG, "got message: " + message.toString());
        try {
            switch(message.getString("type").toLowerCase()){
                case "devices":
                    List<Device> deviceList = new LinkedList<>();
                    Log.d(TAG, "got devices list length = " + message.getJSONArray("devices").length());
                    for(int i = 0; i < message.getJSONArray("devices").length();i++){
                        JSONObject object = message.getJSONArray("devices").getJSONObject(i);
                        Device d = new Device();
                        d.setType(object.getString("type"));
                        d.setName(object.getString("dname"));
                        deviceList.add(d);
                        Log.d(TAG, "Device added: " + d.getName());
                    }
                    setDeviceList(new LinkedList<>(deviceList));
                    setChanged();
                    notifyObservers(message.getString("type"));
                    break;
                case "value":
                    for(Device dev : getDeviceList()){
                        if(message.getString("dname").equals(dev.getName())){
                            switch(dev.getType()){
                                case "integer":
                                    dev.setValue(message.getInt("value"));
                                    Log.d(TAG, "Value " + message.getInt("value") + " for device " + dev.getName());
                                    break;
                                case "boolean":
                                    dev.setValue(message.getBoolean("value"));
                                    Log.d(TAG, "Value " + message.getBoolean("value") + " for device " + dev.getName());
                                    break;
                            }
                            setChanged();
                            notifyObservers(message.getString("type"));
                            break;
                        }
                    }
                    break;
            }
        } catch (JSONException e) {

        }
    }
}
