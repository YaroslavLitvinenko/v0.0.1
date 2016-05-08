package com.himmel.graduate.code.DB.Data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Lyaro on 08.05.2016.
 */
public class Device {
    private int id;
    private String mac;
    private int date;

    public Device(int id, String mac, int date) {
        this.id = id;
        this.mac = mac;
        this.date = date;
    }

    public Device(String mac) {
        this.mac = mac;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public StringProperty macProperty() {
        return new SimpleStringProperty(mac);
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Device device = (Device) o;

        return mac.equals(device.mac);

    }
}
