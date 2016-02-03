package com.himmel.graduate.code.Management;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Lyaro on 03.02.2016.
 */
class MySocket {
    boolean ClServ;

    InetAddress inetAddress;

    public MySocket (InetAddress inetAddress, boolean ClServ){
        this.ClServ = ClServ;
        this.inetAddress = inetAddress;
    }

    public InetAddress getInetAddress (){
        return inetAddress;
    }

    public boolean isClServ (){
        return ClServ;
    }
}
