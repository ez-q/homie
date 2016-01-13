package viktor.at.homieapp;

import java.util.LinkedList;
import java.util.List;

public class Device {
    private String address;
    private String name;
    private List<Object> values;

    public Device(String address, String name){
        this.address = address;
        this.name = name;
        values = new LinkedList<>();
    }

    public Device(){values = new LinkedList();}


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return getName();
    }
}
