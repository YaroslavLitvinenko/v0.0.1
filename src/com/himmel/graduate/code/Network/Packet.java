package com.himmel.graduate.code.Network;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Lyaro on 12.03.2016.
 */
public class Packet implements Serializable {
    private String comand;
    private Object data;
    private String name;

    public Packet (String comand, String name, Object data){
        this.comand = comand;
        this.name = name;
        this.data = data;
    }

    public Packet (String comand, Object data){
        this.comand = comand;
        this.name = null;
        this.data = data;
    }

    public Packet (String comand){
        this.comand = comand;
        name = null;
        data = null;
    }

    public String getComand() {
        return comand;
    }

    public Object getData() {
        return data;
    }

    public String getName (){
        return name;
    }
}
