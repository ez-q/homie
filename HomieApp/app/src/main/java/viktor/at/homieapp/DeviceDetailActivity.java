package viktor.at.homieapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.Observable;
import java.util.Observer;

public class DeviceDetailActivity extends AppCompatActivity implements Observer {
    DeviceFragment fragment;
    Button btReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        /*switch(d.getType()){

        }*/
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment = new DeviceFragment();
        fragment.setDeviceName(d.getName());
        transaction.add(R.id.mainContainer, fragment);
        transaction.commit();
    }

    @Override
    public void update(Observable observable, Object data) {
        if(((String)data).equals("value")){
            fragment.updateValue();
        }
    }
}
