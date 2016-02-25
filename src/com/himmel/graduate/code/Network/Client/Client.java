package com.himmel.graduate.code.Network.Client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Lyaro on 07.02.2016.
 */
public class Client implements Runnable {
    private static final int PORT_TCP = 8034;
    ArrayList<File> files;
    InetAddress address;
    Socket connection;

    public Client (ArrayList<File> files, InetAddress address){
        this.files = files;
        this.address = address;
    }


    @Override
    public void run() {
        ObjectOutputStream out;
        ObjectInputStream in;
        int flag = 5;
        while (true) {
            try {
                connection = new Socket(InetAddress.getLocalHost(), PORT_TCP);
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
                //TODO Допилить проверку
                if (flag!=0){
                    flag--;
                    try {
                        Thread.sleep(12000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    break;
                }
            }

        }
    }
}
