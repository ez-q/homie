package viktor.at.homieapp.Fragments;

import android.support.v4.app.Fragment;;

public abstract class BaseFragment extends Fragment {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String deviceName;

    public boolean viewCreated = false;

    public abstract void updateValue();
    public abstract void updateChart();
}
