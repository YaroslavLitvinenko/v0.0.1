package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Created by Lyaro on 20.12.2015.
 */
class DBConnect implements Runnable {
    ObservableList<MyFolder> folderData = FXCollections.observableArrayList();
    ObservableList<MyFile> fileData = FXCollections.observableArrayList();
    ObservableList<MySetting> settingsData = FXCollections.observableArrayList();

    Thread mainThread;
    Thread settingTread;
    Thread fileThread;

    private Connection conn;
    private Statement statementForFolder;
    private Statement statementForFile;
    private Statement statementForSettings;

    public DBConnect () {
        //Запуск нового потока
        mainThread = new Thread(this);
        mainThread.start();
    }

    //Запуск конструктора в новом потоке
    @Override
    public void run() {
        //Соединение с БД
        conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Создание необходимых таблиц
        try {
            statementForFolder = conn.createStatement();
            statementForFile = conn.createStatement();
            statementForSettings = conn.createStatement();

            //Создание таблицы папок
            statementForFolder.execute("CREATE TABLE if not exists 'Folder' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Path' STRING, 'md5' STRING);");

            //Создание таблицы файлов
            statementForFolder.execute("CREATE TABLE if not exists 'File' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Path' STRING, 'md5' STRING);");

            //Создание таблицы списка настроек
            statementForSettings.execute("CREATE TABLE if not exists 'Settings' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Name' STRING, 'Value' STRING);");
        } catch (SQLException e) {
            System.err.println(e);
        }

        settingTread = new Thread(()->this.CreateAListOfSettings(statementForSettings));
        settingTread.start();
        fileThread = new Thread(()->this.CreateAListOfFile(statementForFile));
        fileThread.start();
        CreateAListOfFolder(statementForFolder);
    }

    //Создание списка файлов
    private void CreateAListOfSettings (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Settings");
            while(resultSet.next())
                settingsData.add(new MySetting(resultSet.getInt("id"), resultSet.getString("Name"), resultSet.getString("Value")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Создание списка папок
    private void CreateAListOfFolder (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Folder");
            while(resultSet.next())
                folderData.add(new MyFolder(resultSet.getInt("id"), resultSet.getString("Path"), resultSet.getString("md5")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Создание списка папок
    private void CreateAListOfFile (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM File");
            while(resultSet.next())
                fileData.add(new MyFile(resultSet.getInt("id"), resultSet.getString("Path"), resultSet.getString("md5")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void newDataOfFolder (MyFolder folder){
        String sql = "INSERT INTO Folder (Path, md5) VALUES ('";
        sql+=folder.getPath()+"', '";
        sql+=folder.getMd5()+"');";
        try {
            statementForFolder.execute(sql);
            folderData.add(folder);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    ObservableList<MySetting> getDataOfSettings (){
        try {
            settingTread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return settingsData;
    }
    ObservableList<MyFolder> getDataOfFolder (){
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderData;
    }
    ObservableList<MyFile> getDataOfFile (){
        try {
            fileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileData;
    }
}
