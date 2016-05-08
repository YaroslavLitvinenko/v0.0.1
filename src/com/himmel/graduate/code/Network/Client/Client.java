package com.himmel.graduate.code.Network.Client;

import com.himmel.graduate.code.DB.Data.Device;
import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.GUI.Controller;
import com.himmel.graduate.code.Network.MySocket;
import com.himmel.graduate.code.Network.Packet;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lyaro on 07.02.2016.
 */
public class Client implements Runnable {
    private static final int PORT_TCP = 8034;
    private InetAddress address;
    private Socket connection;
    private Device device;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private FileManager fileManager;

    private Thread thread;
    private boolean f = false;

    private Controller controller;
    private int progres = 0;
    private double cauntOfSyncFie = 0;

    public Client (FileManager fileManager, MySocket socket, Controller controller){
        out = null;
        in = null;
        this.fileManager = fileManager;
        this.address = socket.getInetAddress();
        this.device = socket.getDevice();
        System.out.println("client");
        this.controller = controller;
        thread = new Thread(this);
        thread.start();
    }

    private void nextStep() {
        progres ++;
        controller.progres.setProgress(progres/cauntOfSyncFie);
    }

    @Override
    public void run() {
        int flag = 6;
        Packet inPack;
        while (0 < --flag) {
            try {
                //Получение и отправка всех действий для полосы загрузки
                cauntOfSyncFie += fileManager.getListDelFiles().size();
                cauntOfSyncFie += fileManager.getListDelFolders().size();
                cauntOfSyncFie += fileManager.getListNewFolders().size();
                cauntOfSyncFie += fileManager.getListNewFiles().size();
                connect(new Packet("cauntFileSync", cauntOfSyncFie));
                cauntOfSyncFie = 6;
                inPack = (Packet) in.readObject();
                cauntOfSyncFie += (double) inPack.getData();
                nextStep();
                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }

                //Удаление удаленных на сервере файлов, с этого ПК
                connect(new Packet("getListDelFiles"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'delFiles' return, count files for delete = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData()) {
                    fileManager.deleteFiles(file.getPath());
                    nextStep();
                }
                System.out.println("Files delete");
                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }

                //Удаление удаленных файлов на этом ПК, с сервера
                connect(new Packet("delFiles", fileManager.getListDelFiles()));
                System.out.println("List files for delete is output, count of files = " +
                        fileManager.getListDelFiles().size());
                in.readObject();
                nextStep();
                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }

                //Удаление удаленных папок на сервере с этого пк
                connect(new Packet("getListDelFolders"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'delFolders' return, count folders for delete = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData()) {
                    fileManager.deleteFolders(file.getPath());
                    nextStep();
                }
                System.out.println("Folders delete");

                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }
                //Удаление удаленных папок на этом ПК с сервера
                connect(new Packet("delFolders", fileManager.getListDelFolders()));
                in.readObject();
                System.out.println("List folders for delete is output, count of folders = " +
                        fileManager.getListDelFolders().size());
                nextStep();

                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }
                //Создание новых папок сервера на этом ПК
                connect(new Packet("getListNayFolders"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'nayFolders' return, count folders on server = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData()) {
                    fileManager.addFolders(file.getPath());
                    nextStep();
                }
                System.out.println("Folders created");

                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }
                //Создание новых папок на этом ПК на сервере
                connect(new Packet("addFolders", fileManager.getListNewFolders()));
                in.readObject();
                System.out.println("List folders for created is output, count of folders = " +
                        fileManager.getListNewFolders().size());
                nextStep();

                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }
                //Создание новых папок с сервера на этом ПК
                connect(new Packet("getListNayFiles"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'nayFolders' return, count folders on server = " +
                        ((ArrayList<MyFile>) inPack.getData()).size());
                ArrayList<MyFile> list = (ArrayList<MyFile>)inPack.getData();
                for (MyFile file : list) {
                    if(!fileManager.isFileSync(file)) {
                        connect(new Packet("getFileData", file));
                        inPack = (Packet) in.readObject();
                        fileManager.newFile((MyFile) inPack.getData(), inPack.getName());
                    }
                    nextStep();
                }
                System.out.println("List 'nayFiles' return, count file on server = " + list.size());

                if (f) {
                    connect(new Packet("exit"));
                    in.readObject();
                    connection.close();
                    out.close();
                    in.close();
                    return;
                }
                //Создание новых файлов на этом ПК на сервере
                connect(new Packet("addFiles", fileManager.getListNewFiles()));
                boolean f = true;
                while (f) {
                    inPack = (Packet) in.readObject();
                    switch (inPack.getComand()) {
                        case "getFileData":
                            connect(new Packet("retFileData", fileManager.getFile(((MyFile) inPack.getData())),
                                    ((MyFile) inPack.getData())));
                            break;
                        case "thatsAll":
                            f = false;
                            break;
                    }
                }
                System.out.println("List file for created is output, count of file = " +
                        fileManager.getListNewFiles().size());
                nextStep();

                Date dateSync = new Date();
                connect(new Packet("time", dateSync));
                in.readObject();
                System.out.println("Time is out");

                //Завершение операции синхронизации
                connect(new Packet("exit"));
                in.readObject();
                System.out.println("exit");

                //Запрос на вызов сборщика мусора, т.к. каждый раз ссылке на
                //пакет присваеваются новые значения, но не утилизируюся старые
                System.gc();

                //Коректное завершение синхронизации
                connection.close();
                out.close();
                in.close();
                controller.progres.setProgress(0);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect (Packet packet) throws IOException {
        connection = new Socket(address, PORT_TCP);
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
        out.flush();
        out.writeObject(packet);
    }

    public void syncOff () {
        try {
            f = true;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
