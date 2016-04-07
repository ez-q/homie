package viktor.at.homieapp.Fragments;

import android.support.v4.app.Fragment;;

/*
Created by: Viktor Bär
This is the base class every other fragment class has to inherit from.
 */
public abstract class BaseFragment extends Fragment {
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String deviceName;

    public boolean viewCreated = false;

    /**
     * In every implementation of this method all values of the fragment must be updated accordingly.
     * @return void
     */
    public abstract void updateValue();

    /**
     * This method is for future chart implementations. Here the chart must be updated accordingly.
     * @return void
     */
    public abstract void updateChart();
}
