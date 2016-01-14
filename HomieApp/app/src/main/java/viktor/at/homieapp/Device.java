package viktor.at.homieapp;

import java.util.LinkedList;
import java.util.List;

public class Device {
    private String name;
    private Object value;
    private String type;

    public Device(String name, Object value){
        this.name = name;
    }

    public Device(){}



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getName();
    }
}
