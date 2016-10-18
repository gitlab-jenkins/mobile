package xyz.homapay.hampay.mobile.android.firebase.service;

/**
 * Created by amir on 10/18/16.
 */
public class Event {

    private int id;
    private String name;
    private ServiceEvent value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceEvent getType() {
        return value;
    }

    public void setType(ServiceEvent value) {
        this.value = value;
    }

}
