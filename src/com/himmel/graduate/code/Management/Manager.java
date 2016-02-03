package com.himmel.graduate.code.Management;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Manager implements Runnable {
    Thread thread;

    public Manager (){
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //5000 = 5 сек.
        Connect connect = new Connect(5000);
        connect.search();
        MySocket mySocket = connect.getConnection();
        if (mySocket.ClServ)
            System.out.println("client " + mySocket.getInetAddress());
        else System.out.println("server " + mySocket.getInetAddress());
    }
}
