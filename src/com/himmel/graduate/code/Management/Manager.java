package com.himmel.graduate.code.Management;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.Network.Connect;
import com.himmel.graduate.code.Network.MySocket;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Manager implements Runnable {
    private Thread thread;
    private DBManagmnet db;

    public Manager (DBManagmnet db){
        this.db = db;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        /*
        //5000 = 5 сек.
        Connect connect = new Connect(5000);
        connect.search();
        MySocket mySocket = connect.getConnection();
        */
        new FileManager(db);
    }
}
