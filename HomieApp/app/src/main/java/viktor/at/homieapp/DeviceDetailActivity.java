package viktor.at.homieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.Observable;
import java.util.Observer;

import viktor.at.homieapp.Fragments.BaseFragment;
import viktor.at.homieapp.Fragments.ButtonFragment;
import viktor.at.homieapp.Fragments.TemperatureFragment;
/*
    Created By: Viktor Bär
 */
public class DeviceDetailActivity extends FragmentActivity implements Observer {
    BaseFragment fragment = null;
    Button btReturn;

    /**
     * Called when the activity is created in the DeviceList activity. This activity displays the
     * corresponding fragment for the selected device. The fragment usually displays the current values
     * or offers buttons to send commands to the server.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        if(findViewById(R.id.mainContainer) != null) {

            if (savedInstanceState != null) {
                return;
            }
            //Here this class is added as an observer to the DeviceRepository. As a result i will be notified when a change occurs.
            DeviceRepository.getInstance().addObserver(this);

            Intent i = getIntent();

            //Retrieving the correct device from the DeviceRepository
            Device d = DeviceRepository.getInstance().getDevice(i.getStringExtra(DeviceListActivity.DEVICENAME));

            btReturn = (Button) findViewById(R.id.btReturn);

            btReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            /*
                The FragmentManager allows dynamic creation of fragments at runtime.
                 First it checks what type the device is and then the correct fragment is called.
             */
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (d.getType().toLowerCase()) {
                case "button":case "led":
                    fragment = new ButtonFragment();
                    fragment.setDeviceName(d.getName());
                    break;
                case "temperature":
                    fragment = new TemperatureFragment();
                    fragment.setDeviceName(d.getName());
                    break;
            }

            WSClient.getInstance().setDataType(d.getName());

            if(fragment != null) {
                transaction.replace(R.id.mainContainer, fragment);
                transaction.commit();
            }
        }
    }

    /**
     * Is called when an changed in the DeviceRepository occurs. First it checks if the change is relevant then it updates the Values.
     * @param observable
     * @param data
     */
    @Override
    public void update(Observable observable, Object data) {
        if (((String) data).equals("data") && fragment != null) {
            fragment.updateValue();
        } else if(((String)data).equals("historyData")){
            fragment.updateChart();
        }
    }
}
