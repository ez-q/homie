package viktor.at.homieapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import viktor.at.homieapp.DeviceRepository;
import viktor.at.homieapp.R;
import viktor.at.homieapp.WSClient;

/**
 * Created by Viktor on 22.02.2016.
 */
public class ButtonFragment extends BaseFragment {
    private TextView tvValues;
    private TextView tv;
    private Button btCurrentState;
    private Button btToggleState;

    public boolean viewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_button, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(getDeviceName());

        tvValues = (TextView) view.findViewById(R.id.tvValuesButton);

        tvValues.setText(DeviceRepository.getInstance().getDevice(getDeviceName()).getValue().toString());

        btCurrentState = (Button) view.findViewById(R.id.btCurrentStateButton);
        btCurrentState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WSClient.getInstance().forceDeviceSendData(getDeviceName());
            }
        });

        btToggleState = (Button) view.findViewById(R.id.btToggleStateButton);
        btToggleState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("dname", getDeviceName());
                Boolean data = !(Boolean)DeviceRepository.getInstance().getDevice(getDeviceName()).getValue();
                map.put("data", data.toString());
                WSClient.getInstance().sendMessage(map);
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
