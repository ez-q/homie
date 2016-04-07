package viktor.at.homieapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import viktor.at.homieapp.DeviceRepository;
import viktor.at.homieapp.R;
import viktor.at.homieapp.WSClient;

/*
    Created by: Viktor Bär
 */
public class TemperatureFragment extends BaseFragment{

    private TextView tvValues;
    private TextView tv;
    private Button btCurrentState;
    private LineChart chart;


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

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValuesTemp);
        //Retrieving the current value for the device from the DeviceRepository
        Object value = DeviceRepository.getInstance().getDevice(getDeviceName()).getValue();
        if(value != null)
            tvValues.setText(value.toString());
        else
            tvValues.setText("no values");

        //Setting the button listener
        btCurrentState = (Button) view.findViewById(R.id.btCurrentStateTemp);
        btCurrentState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSClient.getInstance().forceDeviceSendData(getDeviceName());
            }
        });

        //chart = (LineChart) view.findViewById(R.id.lineChart);
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
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());
    }

    @Override
    public void updateChart() {

    }
}
