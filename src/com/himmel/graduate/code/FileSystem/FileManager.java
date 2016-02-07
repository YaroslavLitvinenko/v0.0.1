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
    ArrayList<MyFolder> listFolders;
    ArrayList<File> listFiles;
    private Thread thread;

    public FileManager (DBManagmnet db){
        listFolders = new ArrayList<>(db.getDataOfFolder());
        listFiles = new ArrayList<>();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //Создание списка новых файлов
        for (MyFolder folder : listFolders){
            try {
                Files.walkFileTree(Paths.get(folder.getPath()), new SimpleFileVisitor<Path>() {
                    //Метод вызывается при обращении к файлу
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                        listFiles.add(new File(file.toString()));
                        //System.out.println(" (" + attr.size() + " bytes)");
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
