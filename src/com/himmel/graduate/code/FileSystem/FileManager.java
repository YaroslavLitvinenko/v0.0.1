package com.himmel.graduate.code.FileSystem;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.*;
import com.himmel.graduate.code.GUI.Controller;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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
    private ArrayList<MyFile> listNewMyFiles;
    //Список созданнных файлов и папок
    private Thread thread;

    private Controller controller;

    //Переменная для доступа к БД
    private DBManagmnet db;

    //TODO Изменить списки, удалять весь путь до папки назначения

    public FileManager (DBManagmnet db, Controller controller){
        //TODO Удалить сон
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.controller = controller;
        this.db = db;
        listNewFolders = new ArrayList<File>();
        listNewFiles = new ArrayList<File>();
        listDelFolders = new ArrayList<File>(db.getDataOfFolders());
        listDelFiles = new ArrayList<File>(db.getDataOfFiles());
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
                    listNewFiles.add(new File(file.toString().substring(mainFolder.getPath().length()+1, file.toString().length())));
                    //System.out.println(" (" + attr.size() + " bytes)");
                    return FileVisitResult.CONTINUE;
                }

                //Метод вызывается при обращении к папке
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (!dir.toString().equals(mainFolder.getPath()))
                        listNewFolders.add(new File(dir.toString().substring(mainFolder.getPath().length()+1, dir.toString().length())));
                    //System.out.format("Directory: %s%n", dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Создание списка удаленных файлов и папок путем удаленя из старых файлов и папок новых
        //listNewFolders.remove(new File(db.getDataOfSettings().get(0).getValue()));
        //TODO переделать операции с удаленными объектами, все покрашется если устройство давно не синхронизилось
        listDelFolders.removeAll(listNewFolders);
        listDelFiles.removeAll(listNewFiles);
        db.updateDateOfFolders(listNewFolders, listDelFolders);
        db.updateDateOfFiles(listNewFiles, listDelFiles);

        //Создание списков новых файлов для передачи
        listNewMyFiles = new ArrayList<MyFile> (db.getDataOfMyFiles());

        controller.listOut.addAll(listNewMyFiles);
    }

    //Возврат списков состояния папки
    public ArrayList<File> getListNewFolders (){
        threadIsOff();
        return listNewFolders;
    }

    public ArrayList<File> getListDelFolders () {
        threadIsOff();
        return listDelFolders;
    }

    public ArrayList<File> getListDelFiles () {
        threadIsOff();
        return listDelFiles;
    }

    public ArrayList<MyFile> getListNewFiles (){
        threadIsOff();
        return listNewMyFiles;
    }

    public boolean threadIsOff (){
        try {
            thread.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Управление файловой сстемой
    public void deleteFiles (String name){
        new File(mainFolder + File.separator + name).delete();
        db.delDataOfFile(new File (name));
        //TODO Переделать списки
        //Переделать эту херьню, сиски не связаны и нужно выкорчевывать вручную
        listNewFiles.remove(listNewFiles.indexOf(new File (name)));
        listNewMyFiles.remove(listNewMyFiles.indexOf(new MyFile(name)));
    }

    public void deleteFolders (String name){
        new File(mainFolder + File.separator + name).delete();
        db.delDataOfFolder(new File(name));
        //TODO Переделать списки
        //Переделать эту херьню, сиски не связаны и нужно выкорчевывать вручную
        listNewFolders.remove(new File(name));
    }

    public void addFolders (String name){
        new File(mainFolder + File.separator + name).mkdirs();
        db.newDataOfFolder(new File(name));
        if (listNewFolders.indexOf(new File (name)) == -1) {
            listNewFolders.add(new File(name));
        }
    }

    //Проверка файла на синхронизированность, и наличие. Если файл синх. возвращ.
    //NeedsSynchronization, если файл нет на компьютере, то DoesNotExist, если
    //файл существует, но требует синхронизации - NeedsSynchronization
    public boolean isFileSync (MyFile inFile) {
        //Файл приходит с путем начиная с главной папки, для
        //кросплатфрменности, на другом компе может быть в
        //другом месе, в линуксе, по другому, файловая
        //система, по этому добавляем путь до главной папки
        if (new File(mainFolder + File.separator + inFile.getPath()).isFile()) {
            MyFile myFile = db.getDataOfFile(inFile);
            if (myFile.getDateHash().getTime() < inFile.getDateHash().getTime()) {
                return false;
            } else return true;
        } else return false;
    }

    public void newFile (MyFile inFile, String inData) {
        File file = new File (mainFolder + File.separator + inFile.getPath());
        try  {
            if (!file.isFile()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file.getPath());
            byte []data = inData.getBytes();
            fileOutputStream.write(data);
            fileOutputStream.close();
            inFile.setDateLastChanges(new Date(file.lastModified()));
            db.updDataOfFile(inFile);
            if (listNewMyFiles.indexOf(inFile) == -1) {
                listNewMyFiles.add(inFile);
                if (listNewFiles.indexOf(new File (inFile.getPath())) == -1) {
                    listNewFiles.add(new File (inFile.getPath()));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFile (MyFile file) {
        try (FileInputStream fileInputStream = new FileInputStream(mainFolder + File.separator + file.getPath())) {
            byte []data = new byte[fileInputStream.available()];
            fileInputStream.read(data);
            String t =  new String(data);
            return t;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error!";
    }
}

