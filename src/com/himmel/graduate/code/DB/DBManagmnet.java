package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.DB.Data.MyFolder;
import com.himmel.graduate.code.DB.Data.MySetting;
import javafx.collections.ObservableList;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lyaro on 30.01.2016.
 */
public class DBManagmnet extends DBConnect {

    public DBManagmnet () {
        super();
    }

    public void newDataOfFolder (File file){
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

        //Добавлене данных в базу
        super.newDataOfFolder(new MyFolder(0, file.getPath(), md5));
    }

    @Override
    public synchronized ObservableList<MySetting> getDataOfSettings (){
        return super.getDataOfSettings();
    }

    @Override
    public synchronized ObservableList<MyFolder> getDataOfFolder (){
        return super.getDataOfFolder();
    }

    @Override
    public synchronized ObservableList<MyFile> getDataOfFile (){
        return super.getDataOfFile();
    }
}
