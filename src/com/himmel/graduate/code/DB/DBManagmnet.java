package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.Device;
import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.DB.Data.MySetting;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Lyaro on 30.01.2016.
 */
public class DBManagmnet extends DBConnect {

    public DBManagmnet () {
        super();
    }

    //Метод для соответствия состояния таблицы File, сотоянию фйлов в папке
    public synchronized void updateDateOfFiles(ArrayList<File> listOfFile, ArrayList<File> listDelFile) {
        listOfFile.forEach(super::newDataOfFile);
        listDelFile.forEach(super::delDataOfFile);
    }

    //Метод для соответствия состояния таблицы Folder, сотоянию папок в папке
    public synchronized void updateDateOfFolders(ArrayList<File> listOfFolder, ArrayList<File> listDelFolder) {
        listOfFolder.forEach(super::newDataOfFolder);
        listDelFolder.forEach(super::delDataOfFolder);
    }

    //Переопределение вызова на скперкласс
    public synchronized MyFile getDataOfFile(MyFile file) {
        return super.getDataOfFile(file);
    }

    @Override
    public synchronized void newDataOfFile(MyFile file){
        super.newDataOfFile(file);
    }

    @Override
    public synchronized void delDataOfFile(File file) {
        super.delDataOfFile(file);
    }

    public synchronized void updDataOfFile(MyFile file) {
        super.newDataOfFile(file);
    }

    @Override
    public synchronized void delDataOfFolder(File file){
        super.delDataOfFolder(file);
    }

    @Override
    public synchronized void newDataOfFolder(File folder) {
        super.newDataOfFolder(folder);
    }

    @Override
    public synchronized void newDataOfDevices(String mac) {
        super.newDataOfDevices(mac);
    }

    @Override
    public synchronized void delDataOfDvices(String mac) {
        super.delDataOfDvices(mac);
    }

    @Override
    public synchronized void newSync(Device device, java.util.Date date) {
        super.newSync(device, date);
    }

    @Override
    public synchronized ObservableList<MySetting> getDataOfSettings(){
        return super.getDataOfSettings();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFolders(){
        return super.getDataOfFolders();
    }

    @Override
    public synchronized ObservableList<File> getDataOfFiles(){
        return super.getDataOfFiles();
    }

    @Override
    public synchronized ObservableList<MyFile> getDataOfMyFiles() {
        return super.getDataOfMyFiles();
    }

    @Override
    public synchronized ObservableList<Device> getDataOfDevice() {
        return super.getDataOfDevice();
    }

    @Override
    public synchronized void setSetting (MySetting setting) {
        super.setSetting(setting);
    }
}
