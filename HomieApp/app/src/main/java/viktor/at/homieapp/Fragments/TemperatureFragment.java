package viktor.at.homieapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import viktor.at.homieapp.DeviceRepository;
import viktor.at.homieapp.R;
import viktor.at.homieapp.WSClient;


public class TemperatureFragment extends BaseFragment{

    private TextView tvValues;
    private TextView tv;
    private Button btCurrentState;


    public boolean viewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperature, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValuesTemp);

        tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());

        btCurrentState = (Button) view.findViewById(R.id.btCurrentStateTemp);
        btCurrentState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSClient.getInstance().forceDeviceSendData(getDeviceName());
            }
        });

        viewCreated = true;
        Log.d("Fragment", "View is created");
        return view;
    }

    @Override
    public void updateValue() {
        if(viewCreated && tvValues != null)
            tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());
    }
}
