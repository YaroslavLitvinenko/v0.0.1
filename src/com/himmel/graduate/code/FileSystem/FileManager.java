package com.himmel.graduate.code.FileSystem;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Lyaro on 06.02.2016.
 */
public class FileManager implements Runnable {
    private File mainFolder;
    //Список удаленных файлов и папок
    private ArrayList<File> listDelFolders;
    private ArrayList<File> listDelFiles;
    //Список файлов и папк на диске
    private ArrayList<File> listNewFolders;
    private ArrayList<File> listNewFiles;
    //Список созданнных файлов и папок
    private Thread thread;

    //Переменная для доступа к БД
    private DBManagmnet db;

    public FileManager (DBManagmnet db){
        //TODO Удалить сон
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.db = db;
        listNewFolders = new ArrayList<File>();
        listNewFiles = new ArrayList<File>();
        listDelFolders = new ArrayList<File>(db.getDataOfFolder());
        listDelFiles = new ArrayList<File>(db.getDataOfFile());
        mainFolder = new File(db.getDataOfSettings().get(0).getValue());
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        //Создание списка новых файлов и папок
        try {
            Files.walkFileTree(Paths.get(mainFolder.getPath()), new SimpleFileVisitor<Path>() {
                //Метод вызывается при обращении к файлу
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
                    listNewFiles.add(file.toFile());
                    //System.out.println(" (" + attr.size() + " bytes)");
                    return FileVisitResult.CONTINUE;
                }

                //Метод вызывается при обращении к папке
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    listNewFolders.add(dir.toFile());
                    //System.out.format("Directory: %s%n", dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Создание списка удаленных файлов и папок путем удаленя из старых файлов и папок новых
        listNewFolders.remove(new File(db.getDataOfSettings().get(0).getValue()));
        listDelFolders.removeAll(listNewFolders);
        listDelFiles.removeAll(listNewFiles);
        db.updateDateOfFolder(listNewFolders, listDelFolders);
        db.updateDateOfFile(listNewFiles, listDelFiles);
    }

    //Возврат списков состояния папки

    public ArrayList<File> getListNewFolders (){
        threadIsOff();
        return listNewFolders;
    }

    public ArrayList<File> getListNewFiles (){
        threadIsOff();
        return listNewFiles;
    }

    public ArrayList<File> getListDelFolders () {
        threadIsOff();
        return listDelFolders;
    }

    public ArrayList<File> getListDelFiles () {
        threadIsOff();
        return listDelFiles;
    }

    private boolean threadIsOff (){
        try {
            thread.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}

