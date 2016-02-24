package viktor.at.homieapp.Fragments;

import android.support.v4.app.Fragment;;
/**
 * Created by Viktor on 21.02.2016.
 */
public class BaseFragment extends Fragment {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String deviceName;

    public boolean viewCreated = false;

    public void updateValue() {
    }
}
