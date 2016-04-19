package com.himmel.graduate.code.Network.Server;

import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.FileSystem.FileManager;
import com.himmel.graduate.code.Network.Packet;
import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Lyaro on 07.02.2016.
 */
public class Server implements Runnable {
    private static final int PORT_TCP = 8034;

    Thread thread;
    FileManager fileManager;

    private ServerSocket server;
    private Socket connection;
    private ObjectOutputStream out;
    private ObjectInputStream in;


    public Server(FileManager fileManager){
        try {
            server = new ServerSocket(PORT_TCP, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fileManager = fileManager;
        out = null;
        in = null;
        thread = new Thread(this);
        System.out.println("server");
        thread.start();
    }

    private void connect() throws IOException{
        connection = server.accept();
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
    }

    @Override
    public void run() {
        try {
            boolean flag = true;
            while (flag){
                connect();
                System.out.println("Connection accept");
                Packet inPack = (Packet)in.readObject();
                switch (inPack.getComand()){
                    case "getListDelFiles": //Удаление удаленных файлов на этом ПК с клиента
                        System.out.println("Comand: getListDelFiles");
                        out.flush();
                        out.writeObject(new Packet("return", fileManager.getListDelFiles()));
                        System.out.println("List files for delete is output, count of files = " + fileManager.getListDelFiles().size());
                        break;
                    case "delFiles": //Удаление удаленных файлов на клиенте на этом пк
                        System.out.println("Comand: delFiles\nList 'delFiles' return, count files for delete = " + ((ArrayList<File>)inPack.getData()).size());
                        for (File file : (ArrayList<File>)inPack.getData())
                            fileManager.deleteFiles(file.getName());
                        System.out.println("Files delete");
                        out.flush();
                        out.writeObject(new Packet("ok"));
                        break;
                    case "getListDelFolders": //Удаление удаленных папок на этом ПК с клиента
                        System.out.println("Comand: getListDelFolders");
                        out.flush();
                        out.writeObject(new Packet("return", fileManager.getListDelFolders()));
                        System.out.println("List folders for delete is output, count of folders = " + fileManager.getListDelFolders().size());
                        break;
                    case "delFolders": //Удаление удаленных папок на клиенте на этом пк
                        System.out.println("Comand: delFolders\nList 'delFolders' return, count folders for delete = " + ((ArrayList<File>)inPack.getData()).size());
                        for (File file : (ArrayList<File>)inPack.getData())
                            fileManager.deleteFolders(file.getName());
                        System.out.println("Files delete");
                        out.flush();
                        out.writeObject(new Packet("ok"));
                        break;
                    case "getListNayFolders": //Добавление новых папок на этом ПК с клиента
                        System.out.println("Comand: getListNayFolders");
                        out.flush();
                        out.writeObject(new Packet("return", fileManager.getListNewFolders()));
                        System.out.println("List folders for created is output, count of folders = " + fileManager.getListNewFolders().size());
                        break;
                    case "addFolders":
                        System.out.println("Comand: addFolders\nList 'newFolders' return, count folders for created = " + ((ArrayList<File>)inPack.getData()).size());
                        for (File file : (ArrayList<File>)inPack.getData())
                            fileManager.addFolders(file.getName());
                        System.out.println("Folder created");
                        out.flush();
                        out.writeObject(new Packet("ok"));
                        break;
                    case "getListNayFiles":
                        System.out.println("Comand: getListNayFolders");
                        out.flush();
                        out.writeObject(new Packet("return", fileManager.getListNewFiles()));
                        System.out.println("List file for created is output, count of file = " + fileManager.getListNewFiles().size());
                        break;
                    case "getFileData":
                        System.out.println("Comand: getFileData");
                        out.flush();
                        out.writeObject(new Packet("retFileData", fileManager.getFile(((MyFile) inPack.getData())),((MyFile) inPack.getData())));
                        System.out.println("Data of file: " + ((MyFile) inPack.getData()).getPath() + "is out.");
                        break;
                    case "addFiles":
                        System.out.println("Comand: addFiles");
                        ArrayList<MyFile> list = (ArrayList<MyFile>)inPack.getData();
                        for (MyFile file : list) {
                            if (!fileManager.isFileSync(file)) {
                                out.flush();
                                out.writeObject(new Packet("getFileData", file));
                                connect();
                                inPack = (Packet)in.readObject();
                                fileManager.newFile((MyFile) inPack.getData(), inPack.getName());
                            }
                        }
                        System.out.println("File created, caunt new file on server = " + list.size());
                        out.flush();
                        out.writeObject(new Packet("thatsAll"));
                        break;
                    case "exit":
                        out.flush();
                        out.writeObject("ok");
                        flag = false;
                        System.out.println("Comand: exit");
                        break;
                }
            }
            connection.close();
            in.close();
            out.close();
            System.out.println("exit");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
