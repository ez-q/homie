package viktor.at.homieapp;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Device {
    private String name;
    private Object value;
    private String type;
    private HashMap<Date, Object> history;

    public Device(String name, Object value){
        this.name = name;
    }

    public Device(){}

    public HashMap<Date, Object> getHistory() {
        return history;
    }

    public void setHistory(HashMap<Date, Object> history) {
        this.history = history;
    }

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
