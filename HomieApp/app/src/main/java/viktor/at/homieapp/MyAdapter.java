package viktor.at.homieapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class MyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Device> devices;
    private static LayoutInflater inflater=null;

    public MyAdapter(Activity a, ArrayList<Device> devices){
        this.activity = a;
        this.devices = devices;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null)
            view = inflater.inflate(R.layout.list_row, null);

        TextView dName = (TextView)view.findViewById(R.id.listDName);
        dName.setText(devices.get(position).getName());
        ImageView v = (ImageView)view.findViewById(R.id.list_image);
        switch(devices.get(position).getType()) {
            case "temperature":
            v.setImageResource(R.drawable.temperature); break;
            case "button":
                v.setImageResource(R.drawable.button); break;
            default:break;
        }

        return view;
    }
}
