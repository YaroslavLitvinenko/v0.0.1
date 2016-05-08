package com.himmel.graduate.code.Network;

import com.himmel.graduate.code.DB.Data.Device;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lyaro on 03.02.2016.
 */
public class MySocket {
    private boolean ClServ;
    private InetAddress inetAddress;
    private Device device;

    public MySocket (InetAddress inetAddress, boolean ClServ, Device device){
        this.ClServ = ClServ;
        this.inetAddress = inetAddress;
        this.device = device;
    }

    public InetAddress getInetAddress (){
        return inetAddress;
    }

    public boolean isClServ (){
        return ClServ;
    }

    public Device getDevice() {
        return device;
    }
}
