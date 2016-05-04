package com.himmel.graduate.code.Management;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.MySetting;
import com.himmel.graduate.code.GUI.Controller;
import com.himmel.graduate.code.GUI.MessageBox;
import com.himmel.graduate.code.Network.Client.*;
import com.himmel.graduate.code.Network.Server.*;
import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.Network.Connect;
import com.himmel.graduate.code.Network.MySocket;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Lyaro on 28.01.2016.
 */
public class Manager implements Runnable {
    private Thread thread;
    private DBManagmnet db;

    private Connect connect;
    private FileManager fileManager;
    private Client client;
    private Controller controller;

    private boolean flag = true;

    public Manager (DBManagmnet db, Controller controller){
        this.db = db;
        this.controller = controller;
        //TODO непашет!
        File mainFolder = new File (db.getDataOfSettings().get(0).getValue());
        if (!mainFolder.isDirectory()) {
            try {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                while (true) {
                    File file = directoryChooser.showDialog(new Stage());
                    if (file != null) {
                        file = new File(file.getPath() + File.separator + "test.txt");
                        if (file.createNewFile()) {
                            file.delete();
                            file = new File(file.getPath().substring(0, (file.getPath().length() - 9)));

                            db.setSetting(new MySetting(0, "mainPages", file.getPath()));
                            controller.pathToFile.setText(mainFolder.getPath());
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //5000 = 5 сек.
        connect = new Connect(5000);
        connect.search();
        fileManager = new FileManager(db, controller);
        MySocket socket = connect.getConnection();
        if (flag) {
            if (socket.isClServ()){
                client = new Client(fileManager, socket.getInetAddress(), controller);
            }
            else new Server(fileManager, controller);
        } else return;
    }

    public void startSync () {
        if (!thread.isAlive())
            thread.start();
    }

    public void stopSync () {
        if (thread.isAlive()) {
            flag = false;
            if (client != null)
                client.syncOff();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
