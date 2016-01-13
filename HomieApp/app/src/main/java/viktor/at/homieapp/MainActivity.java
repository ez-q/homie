package viktor.at.homieapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "at.viktor.WSClientTest";

    private final WebSocketConnection mConnection = new WebSocketConnection();

    List<Device> devices;

    LinearLayout fragContainer;
    List<DeviceFragment> fragments;

    private void start() {

        final String wsuri = "ws://10.0.2.2:50555";
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
                        switch(message.getString("type").toLowerCase()){
                            case "device":
                                Device d = new Device();
                                d.setAddress(message.getString("address"));
                                d.setName(message.getString("name"));
                                devices.add(d);
                                addNewDevice(d);
                                Log.d(TAG, "Device added: " + d.getName());
                                //Log.d(TAG,"" + fragments.get(fragments.size()-1).appendValue(12));
                                break;
                            case "value":
                                for(Device dev : devices){
                                    if(message.getString("name").equals(dev.getName())){
                                        dev.getValues().add(message.getInt("value"));
                                        Log.d(TAG, "Value " + message.getInt("value") + " for device " + dev.getName());
                                        addValueForDevice(dev, message.getInt("value"));
                                        break;
                                    }
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        Log.d(TAG,"not a JSON string");
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost. "+reason);
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        devices = new LinkedList<>();
        fragments = new LinkedList<>();
        start();

        fragContainer = (LinearLayout) findViewById(R.id.fragContainer);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void addNewDevice(Device d){

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setDeviceName(d.getName());
        fragments.add(fragment);
        transaction.add(R.id.fragContainer, fragment);
        transaction.commit();

        /*
        // Find the ScrollView
        ScrollView sv = (ScrollView) findViewById(R.id.scrollView);

        // Create a LinearLayout element
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        // Add text
        TextView tv = new TextView(this);
        tv.setText(d.getName());
        ll.addView(tv);

        EditText et = new EditText(this);


        // Add the LinearLayout element to the ScrollView
        sv.addView(ll);

        // Display the view
        //setContentView(v);*/
    }

    public void addValueForDevice(Device d, int i){
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
    }



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
}
