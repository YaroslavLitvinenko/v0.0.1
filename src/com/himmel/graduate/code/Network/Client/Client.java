package com.himmel.graduate.code.Network.Client;

import com.himmel.graduate.code.FileSystem.FileManager;

import java.io.*;
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

    ObjectOutputStream out;
    ObjectInputStream in;

    FileManager fileManager;

    public Client (FileManager fileManager, InetAddress address){
        out = null;
        in = null;
        this.fileManager = fileManager;
        this.address = address;
        new Thread(this).start();
    }


    @Override
    public void run() {
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

    public Object sendMasage (Object object){
        try {
            out.flush();
            out.writeObject(object);
            return in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "Error";
    }
}
