package viktor.at.homieapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import viktor.at.homieapp.Fragments.TemperatureFragment;

public class MainActivity extends AppCompatActivity implements Observer {

    private static final String TAG = "at.viktor.WSClientTest";


    List<Device> devices;

    LinearLayout fragContainer;
    List<TemperatureFragment> fragments;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        devices = new LinkedList<>();
        fragments = new LinkedList<>();
        fragContainer = (LinearLayout) findViewById(R.id.fragContainer);
        WSClient.getInstance().addObserver(this);
    }

    void addNewDevice(Device d){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        TemperatureFragment fragment = new TemperatureFragment();
        fragment.setDeviceName(d.getName());
        fragments.add(fragment);
        transaction.add(R.id.fragContainer, fragment);
        transaction.commit();
    }

    /*public void addValueForDevice(Device d, int i){
        for(DeviceFragment frag : fragments) {
            if (frag.getDeviceName().equals(d.getName())) {
                if (frag.viewCreated) {
                    frag.appendValue(i);
                } else {
                    Log.d(TAG, "value " + i + " added");
                    frag.valuesToBeAdded.add(i);
                }
                break;
            }
        }
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object data) {
        JSONObject message = (JSONObject)data;
        try {
            switch(message.getString("type").toLowerCase()){
                case "device":
                    Device d = new Device();
                    d.setType(message.getString("type"));
                    d.setName(message.getString("name"));
                    devices.add(d);
                    //addNewDevice(d);
                    Log.d(TAG, "Device added: " + d.getName());
                    //Log.d(TAG,"" + fragments.get(fragments.size()-1).appendValue(12));
                    break;
                case "value":
                    for(Device dev : devices){
                        if(message.getString("name").equals(dev.getName())){
                            switch(dev.getType()){
                                case "integer":
                                    dev.setValue(message.getInt("value"));
                                    break;
                                case "boolean":
                                    dev.setValue(message.getBoolean("value"));
                                    break;
                            }
                            //Log.d(TAG, "Value " + message.getInt("value") + " for device " + dev.getName());
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
