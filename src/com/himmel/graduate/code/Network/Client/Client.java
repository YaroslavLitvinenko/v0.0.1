package com.himmel.graduate.code.Network.Client;

import com.himmel.graduate.code.DB.Data.MyFile;
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
        int flag = 6;
        Packet inPack;
        while (0 < --flag) {
            try {
                //�������� ��������� ������ �� ������� �� ���� ��
                connect(new Packet("getListDelFiles"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'delFiles' return, count files for delete = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData())
                    fileManager.deleteFiles(file.getName());
                System.out.println("Files delete");

                //�������� ��������� ������ �� ���� �� � �������
                connect(new Packet("delFiles", fileManager.getListDelFiles()));
                System.out.println("List files for delete is output, count of files = " +
                        fileManager.getListDelFiles().size());
                in.readObject();

                //�������� ��������� ����� �� ������� �� ���� ��
                connect(new Packet("getListDelFolders"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'delFolders' return, count folders for delete = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData())
                    fileManager.deleteFolders(file.getName());
                System.out.println("Folders delete");

                //�������� ��������� ����� �� ���� �� � �������
                connect(new Packet("delFolders", fileManager.getListDelFolders()));
                in.readObject();
                System.out.println("List folders for delete is output, count of folders = " +
                        fileManager.getListDelFolders().size());

                //�������� ����� ����� ������� �� ���� ��
                connect(new Packet("getListNayFolders"));
                inPack = (Packet) in.readObject();
                System.out.println("List 'nayFolders' return, count folders on server = " +
                        ((ArrayList<File>) inPack.getData()).size());
                for (File file : (ArrayList<File>) inPack.getData())
                    fileManager.addFolders(file.getName());
                System.out.println("Folders created");

                //�������� ����� ����� �� ���� �� �� �������
                connect(new Packet("addFolders", fileManager.getListNewFolders()));
                in.readObject();
                System.out.println("List folders for created is output, count of folders = " +
                        fileManager.getListNewFolders().size());

                //�������� ����� ����� � ������� �� ���� ��
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
                }
                System.out.println("List 'nayFiles' return, count file on server = " + list.size());

                //�������� ����� ������ �� ���� �� �� �������
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

                //���������� �������� �������������
                connect(new Packet("exit"));
                in.readObject();

                //������ �� ����� �������� ������, �.�. ������ ��� ������ ��
                //����� ������������� ����� ��������, �� �� ������������ ������
                System.gc();

                //��������� ���������� �������������
                connection.close();
                out.close();
                in.close();
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
}
