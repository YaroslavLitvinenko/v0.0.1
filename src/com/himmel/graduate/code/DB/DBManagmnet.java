package com.himmel.graduate.code.DB;

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

    public String getMD5 (File file){
        //Создание ключа md5
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(file.getPath().getBytes());
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        BigInteger bigInt = new BigInteger(1, digest);
        String md5 = bigInt.toString(16);
        while( md5.length() < 32 ){
            md5 = "0" + md5;
        }

        return md5;
    }

    //Метод для соответствия состояния таблицы File, сотоянию фйлов в папке
    public synchronized void updateDateOfFile (ArrayList<File> listOfFile, ArrayList<File> listDelFile) {
        for(File file : listOfFile)
            super.newDataOfFile(file);
        for(File delFile : listDelFile)
            super.delDataOfFile(delFile);
    }

    //Метод для соответствия состояния таблицы Folder, сотоянию папок в папке
    public synchronized void updateDateOfFolder (ArrayList<File> listOfFolder, ArrayList<File> listDelFolder){
        for (File folder : listOfFolder)
            super.newDataOfFolder(folder);
        for (File delFolder : listDelFolder)
            super.delDataOfFolder(delFolder);
    }


    @Override
    public synchronized ObservableList<MySetting> getDataOfSettings (){
        return super.getDataOfSettings();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFolder (){
        return super.getDataOfFolder();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFile (){
        return super.getDataOfFile();
    }
}
