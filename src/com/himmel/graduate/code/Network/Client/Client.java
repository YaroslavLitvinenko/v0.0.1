package com.himmel.graduate.code.Network.Client;

import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.Network.Packet;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Lyaro on 07.02.2016.
 */
public class Client implements Runnable {
    private static final int PORT_TCP = 8034;
    private ArrayList<File> files;
    private InetAddress address;
    private Socket connection;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private FileManager fileManager;

    public Client (FileManager fileManager, InetAddress address){
        out = null;
        in = null;
        this.fileManager = fileManager;
        this.address = address;
        System.out.println("client");
        new Thread(this).start();
    }


    @Override
    public void run() {
        int flag = 5;
        Packet inPack;
        try {
            //Удаление удаленных файлов на сервере на этом пк
            connect(new Packet("getListDelFiles"));
            inPack = (Packet)in.readObject();
            System.out.println("List 'delFiles' return, count files for delete = " + ((ArrayList<File>)inPack.getData()).size());
            for (File file : (ArrayList<File>)inPack.getData())
                fileManager.deleteFiles(file.getName());
            System.out.println("Files delete");

            //Удаление удаленных файлов на этом ПК с сервера
            connect(new Packet("delFiles", fileManager.getListDelFiles()));
            System.out.println("List files for delete is output, count of files = " + fileManager.getListDelFiles().size());
            in.readObject();

            //Удаление удаленных папок на сервере на этом пк
            connect(new Packet("getListDelFolders"));
            inPack = (Packet)in.readObject();
            System.out.println("List 'delFolders' return, count folders for delete = " + ((ArrayList<File>)inPack.getData()).size());
            for (File file : (ArrayList<File>)inPack.getData())
                fileManager.deleteFolders(file.getName());
            System.out.println("Folders delete");

            //Удаление удаленных папок на этом ПК с сервера
            connect(new Packet("delFolders", fileManager.getListDelFolders()));
            in.readObject();
            System.out.println("List folders for delete is output, count of folders = " + fileManager.getListDelFolders().size());

            //Создание новых папок сервера на этом ПК
            connect(new Packet("getListNayFolders"));
            inPack = (Packet)in.readObject();
            System.out.println("List 'nayFolders' return, count folders on server = " + ((ArrayList<File>)inPack.getData()).size());
            for (File file : (ArrayList<File>)inPack.getData())
                fileManager.addFolders(file.getName());
            System.out.println("Folders created");

            //Создание новых папок на этом ПК на сервере
            connect(new Packet("addFolders", fileManager.getListNewFolders()));
            in.readObject();
            System.out.println("List folders for created is output, count of folders = " + fileManager.getListNewFolders().size());
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
                return;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void connect (Packet packet) throws IOException {
        connection = new Socket(InetAddress.getLocalHost(), PORT_TCP);
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
        out.flush();
        out.writeObject(packet);
    }

    public void sendMessage (Packet packet){

    }
}
