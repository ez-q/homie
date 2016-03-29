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


public class TemperatureFragment extends BaseFragment{

    private TextView tvValues;
    private TextView tv;
    private Button btCurrentState;
    private LineChart chart;


    public boolean viewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValuesTemp);
        System.out.println(DeviceRepository.getInstance().getDevice(getDeviceName()));
        Object value = DeviceRepository.getInstance().getDevice(getDeviceName()).getValue();
        if(value != null)
            tvValues.setText(value.toString());
        else
            tvValues.setText("no values");

        btCurrentState = (Button) view.findViewById(R.id.btCurrentStateTemp);
        btCurrentState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSClient.getInstance().forceDeviceSendData(getDeviceName());
            }
        });

        chart = (LineChart) view.findViewById(R.id.lineChart);
        viewCreated = true;
        Log.d("Fragment", "View is created");
        return view;
    }

    @Override
    public void updateValue() {
        if(viewCreated && tvValues != null)
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());
    }

    @Override
    public void updateChart() {

    }
}
