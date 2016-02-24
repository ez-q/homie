package viktor.at.homieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import viktor.at.homieapp.Fragments.BaseFragment;
import viktor.at.homieapp.Fragments.ButtonFragment;
import viktor.at.homieapp.Fragments.TemperatureFragment;

public class DeviceDetailActivity extends FragmentActivity implements Observer {
    BaseFragment fragment = null;
    Button btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        if(findViewById(R.id.mainContainer) != null) {

            if (savedInstanceState != null) {
                return;
            }
            DeviceRepository.getInstance().addObserver(this);

            Intent i = getIntent();
            Device d = DeviceRepository.getInstance().getDevice(i.getStringExtra(DeviceListActivity.DEVICENAME));

            btReturn = (Button) findViewById(R.id.btReturn);

            btReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (d.getType().toLowerCase()) {
                case "boolean":
                    fragment = new ButtonFragment();
                    fragment.setDeviceName(d.getName());
                    break;
                case "integer":
                    fragment = new TemperatureFragment();
                    fragment.setDeviceName(d.getName());
                    break;
            }

            if(fragment != null) {
                transaction.add(R.id.mainContainer, fragment);
                transaction.commit();
            }
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if(((String)data).equals("value") && fragment != null){
            fragment.updateValue();
        }
    }
}
