package viktor.at.homieapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;


public class DeviceFragment extends Fragment{

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    // TODO: Rename and change types of parameters
    private String deviceName;

    private TextView tvValues;
    private TextView tv;


    public boolean viewCreated = false;
    public List<Integer> valuesToBeAdded;


    public DeviceFragment() {
        valuesToBeAdded = new LinkedList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        tv = (TextView) view.findViewById(R.id.tvName);
        tv.setText(deviceName);

        tvValues = (TextView) view.findViewById(R.id.tvValues);
        /*for(int i : valuesToBeAdded){
            appendValue(i);
        }*/
        tvValues.setText(DeviceRepository.getInstance().getDevice(deviceName).getValue().toString());
        viewCreated = true;
        Log.d("Fragment","View is created");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void updateValue() {
        tvValues.setText(DeviceRepository.getInstance().getDevice(deviceName).getValue().toString());
    }
}
