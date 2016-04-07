package viktor.at.homieapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import viktor.at.homieapp.Device;
import viktor.at.homieapp.DeviceRepository;
import viktor.at.homieapp.R;
import viktor.at.homieapp.WSClient;
/*
    Created by: Viktor Bär
 */

public class ButtonFragment extends BaseFragment {
    private TextView tvValues;
    private TextView tv;
    private Button btCurrentState;
    private Button btToggleStateOn;
    private Button btToggleStateOff;

    public boolean viewCreated = false;

    /**
     * When the fragment is created all UI-objects are retrieved from the layout so the values and the listener can be set.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_button, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValuesButton);

        //Retrieving the current value for the device from the DeviceRepository
        if(DeviceRepository.getInstance().getDevice(getDeviceName()) != null)
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());

        //Setting all the listener
        btCurrentState = (Button) view.findViewById(R.id.btCurrentStateButton);
        btCurrentState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSClient.getInstance().forceDeviceSendData(getDeviceName());
            }
        });

        btToggleStateOn = (Button) view.findViewById(R.id.btToggleStateButtonOn);
        btToggleStateOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceRepository.getInstance().getDevice(getDeviceName()) != null) {
                    WSClient.getInstance().forceDeviceToExecuteCommand(getDeviceName(), true);
                }
            }
        });
        btToggleStateOff = (Button) view.findViewById(R.id.btToggleStateButtonOff);
        btToggleStateOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceRepository.getInstance().getDevice(getDeviceName()) != null) {
                    WSClient.getInstance().forceDeviceToExecuteCommand(getDeviceName(), false);
                }
            }
        });
        viewCreated = true;
        Log.d("Fragment", "View is created");
        return view;
    }

    /**
     * An implementation of the method from the class BaseFragment.
     */
    @Override
    public void updateValue() {
        if(viewCreated && tvValues != null && DeviceRepository.getInstance().getDevice(getDeviceName()) != null)
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName())
                    .getValue().toString());
    }

    @Override
    public void updateChart() {

    }
}
