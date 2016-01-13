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


public class DeviceFragment extends Fragment implements IAppendFragmentValues {

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    // TODO: Rename and change types of parameters
    private String deviceName;

    private EditText etValues;
    private TextView tv;

    public boolean viewCreated = false;
    public List<Integer> valuesToBeAdded;

    private OnFragmentInteractionListener mListener;

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
        return inflater.inflate(R.layout.fragment_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv = (TextView) getView().findViewById(R.id.tvName);
        tv.setText(deviceName);

        etValues = (EditText) getView().findViewById(R.id.etValues);
        for(int i : valuesToBeAdded){
            appendValue(i);
        }
        viewCreated = true;
        Log.d("Fragment","View is created");
        Log.d("Fragment", "Values to be added: "+valuesToBeAdded.size());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void appendValue(int i) {
        if(etValues == null)
            Log.d("Fragment","ET is null");
        else
            etValues.append(String.valueOf(i)+" ");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
