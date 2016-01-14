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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class DeviceListActivity extends ListActivity implements Observer {

    private static final String TAG = "at.viktor.WSClientTest";
    public static final String DEVICENAME = "devicename";

    ArrayList<Device> devices = new ArrayList<>();
    ArrayAdapter<Device> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        DeviceRepository.getInstance().addObserver(this);

        adapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1,devices);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), DeviceDetailActivity.class);
                i.putExtra(DEVICENAME,((Device)getListView().getItemAtPosition(position)).getName());
                startActivity(i);
            }
        });
    }



    public void addItem(Device d){
        devices.add(d);
        adapter.notifyDataSetChanged();
    }

    public void updateDevices(){
        this.devices.clear();
        this.devices.addAll(DeviceRepository.getInstance().getDeviceList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void update(Observable observable, Object data) {
        if(((String)data).equals("devices")){
            updateDevices();
        }
    }
}
