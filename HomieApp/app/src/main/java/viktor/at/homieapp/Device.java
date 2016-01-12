package viktor.at.homieapp;

import java.util.LinkedList;
import java.util.List;

public class Device {
    private String address;
    private String name;
    private List<Integer> values;

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

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

}
