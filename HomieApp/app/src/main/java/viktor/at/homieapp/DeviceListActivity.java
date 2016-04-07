package viktor.at.homieapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
/*
    Created by: Viktor Bär
 */
public class DeviceListActivity extends ListActivity implements Observer {

    private static final String TAG = "at.viktor.WSClientTest";
    public static final String DEVICENAME = "devicename";

    ArrayList<Device> devices = new ArrayList<>();
    MyAdapter myAdapter;
    Button btReturn;

    /**
     * Called when the activity is created from the Connect activity. This Activity displays all connected Devices.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //Adding this class as an observer to the DeviceRepository, so you get notified when an change occurs and the device list gets updated.
        DeviceRepository.getInstance().deleteObserver(this);
        DeviceRepository.getInstance().addObserver(this);

        /*
            Finishes the activity and returns to the Connect activity
         */
        btReturn = (Button)findViewById(R.id.btReturnLv);
        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
            Setting a custom list adapter because then you can control the list item design.
         */
        myAdapter = new MyAdapter(this, devices);
        setListAdapter(myAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DeviceDetailActivity.class);
                System.out.println((Device)getListView().getItemAtPosition(position));
                i.putExtra(DEVICENAME,(((Device)getListView().getItemAtPosition(position)).getName()));
                startActivity(i);
            }
        });
        if(WSClient.getInstance().getmConnection().isConnected()){
            updateDevices();
        }
    }

    /**
     * Adds an device to the current device list
     * @param device
     */
    public void addItem(Device device){
        devices.add(device);
        myAdapter.notifyDataSetChanged();
    }

    /**
     * Clears the current device list and gets the current list from the DeviceRepository
     */
    public void updateDevices(){
        this.devices.clear();
        this.devices.addAll(DeviceRepository.getInstance().getDeviceList());
        myAdapter.notifyDataSetChanged();
    }

    /**
     * Is called when an changed in the DeviceRepository occurs. First it checks if the change is relevant and updates the devices if so.
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        if(((String)data).equals("devices")){
            updateDevices();
        }
    }
}
