package com.himmel.graduate.code.Management;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.Network.Client.*;
import com.himmel.graduate.code.Network.Server.*;
import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.Network.Connect;
import com.himmel.graduate.code.Network.MySocket;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Manager implements Runnable {
    private Thread thread;
    private DBManagmnet db;

    private Connect connect;
    private FileManager fileManager;

    public Manager (DBManagmnet db){
        this.db = db;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //5000 = 5 сек.
        //connect = new Connect(5000);
        //connect.search();
        fileManager = new FileManager(db);
        //MySocket socket = connect.getConnection();
        ArrayList<File> allListFile = fileManager.getListFiles();
        //if (socket.isClServ())
        //    new Client(allListFile, socket.getInetAddress());
        //else new Server();
    }
}
