package com.himmel.graduate.code.FileSystem;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

/**
 * Created by Lyaro on 06.02.2016.
 */
public class FileManager implements Runnable {
    private File mainFolder;
    private ArrayList<MyFolder> listDelFolders;
    private ArrayList<MyFile> listDelFiles;
    private ArrayList<MyFolder> listNewFolders;
    private ArrayList<MyFile> listNewFiles;
    private ArrayList<MyFolder> listOldFolders;
    private ArrayList<MyFile> listOldFiles;
    private Thread thread;

    public FileManager (DBManagmnet db){
        //TODO Удалить сон
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        listDelFolders = new ArrayList<MyFolder>();
        listDelFiles = new ArrayList<MyFile>();
        listNewFolders = new ArrayList<MyFolder>();
        listNewFiles = new ArrayList<MyFile>();
        mainFolder = new File(db.getDataOfSettings().get(0).getValue());
        listOldFolders = new ArrayList<>(db.getDataOfFolder());
        listOldFiles = new ArrayList<>(db.getDataOfFile());
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //Создание списка новых файлов
        try {
            System.out.println(mainFolder.getName());
            Files.walkFileTree(Paths.get(mainFolder.getPath()), new SimpleFileVisitor<Path>() {
                //Метод вызывается при обращении к файлу
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    listNewFiles.add(new File(file.toString()));
                    //System.out.println(" (" + attr.size() + " bytes)");
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    System.out.format("Directory: %s%n", dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<File> getListFiles (){
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listNewFiles;
    }
}

