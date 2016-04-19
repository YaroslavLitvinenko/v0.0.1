package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.DB.Data.MySetting;
import javafx.collections.ObservableList;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by Lyaro on 30.01.2016.
 */
public class DBManagmnet extends DBConnect {

    public DBManagmnet () {
        super();
    }

    //Метод для соответствия состояния таблицы File, сотоянию фйлов в папке
    public synchronized void updateDateOfFiles (ArrayList<File> listOfFile, ArrayList<File> listDelFile) {
        for(File file : listOfFile)
            super.newDataOfFile(file);
        for(File delFile : listDelFile)
            super.delDataOfFile(delFile);
    }

    //Метод для соответствия состояния таблицы Folder, сотоянию папок в папке
    public synchronized void updateDateOfFolders (ArrayList<File> listOfFolder, ArrayList<File> listDelFolder){
        for (File folder : listOfFolder)
            super.newDataOfFolder(folder);
        for (File delFolder : listDelFolder)
            super.delDataOfFolder(delFolder);
    }

    //Переопределение вызова на скперкласс
    public synchronized MyFile getDataOfFile (MyFile file) {
        return super.getDataOfFile(file);
    }

    @Override
    public synchronized void newDataOfFile (MyFile file){
        super.newDataOfFile(file);
    }

    @Override
    public synchronized void delDataOfFile (File file) {
        super.delDataOfFile(file);
    }

    public synchronized void updDataOfFile (MyFile file) {
        super.newDataOfFile(file);
    }

    @Override
    public synchronized void delDataOfFolder (File file){
        super.delDataOfFolder(file);
    }

    @Override
    public synchronized void newDataOfFolder (File folder) {
        super.newDataOfFolder(folder);
    }


    @Override
    public synchronized ObservableList<MySetting> getDataOfSettings (){
        return super.getDataOfSettings();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFolders (){
        return super.getDataOfFolders();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFiles (){
        return super.getDataOfFiles();
    }

    @Override
    public synchronized ObservableList<MyFile> getDataOfMyFiles () {
        return super.getDataOfMyFiles();
    }

    @Override
    public synchronized void setSetting (MySetting setting) {
        super.setSetting(setting);
    }
}
