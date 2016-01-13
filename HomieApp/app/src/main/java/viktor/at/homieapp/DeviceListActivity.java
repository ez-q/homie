package viktor.at.homieapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class DeviceListActivity extends ListActivity implements Observer {

    private static final String TAG = "at.viktor.WSClientTest";

    ArrayList<Device> devices = new ArrayList<>();
    ArrayAdapter<Device> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        WSClient.getInstance().addObserver(this);

        adapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1,devices);
        setListAdapter(adapter);
    }

    public void addItem(Device d){
        devices.add(d);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        JSONObject message = (JSONObject)data;
        try {
            switch(message.getString("type").toLowerCase()){
                case "device":
                    Device d = new Device();
                    d.setAddress(message.getString("address"));
                    d.setName(message.getString("name"));
                    //devices.add(d);
                    addItem(d);
                    Log.d(TAG, "Device added: " + d.getName());
                    //Log.d(TAG,"" + fragments.get(fragments.size()-1).appendValue(12));
                    break;
                case "value":
                    for(Device dev : devices){
                        if(message.getString("name").equals(dev.getName())){
                            dev.getValues().add(message.getInt("value"));
                            Log.d(TAG, "Value " + message.getInt("value") + " for device " + dev.getName());
                            //addValueForDevice(dev, message.getInt("value"));
                            break;
                        }
                    }
                    break;
            }
        } catch (JSONException e) {

        }
    }
}
