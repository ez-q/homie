package viktor.at.homieapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import viktor.at.homieapp.DeviceRepository;
import viktor.at.homieapp.R;


public class TemperatureFragment extends BaseFragment{

    private TextView tvValues;
    private TextView tv;


    public boolean viewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValues);

        tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());
        viewCreated = true;
        Log.d("Fragment", "View is created");
        return view;
    }

    @Override
    public void updateValue() {
        if(viewCreated)
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());
    }
}
