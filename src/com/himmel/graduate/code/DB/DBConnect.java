package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;

/**
 * Created by Lyaro on 20.12.2015.
 */
class DBConnect implements Runnable {
    ObservableList<File> folderData = FXCollections.observableArrayList();
    ObservableList<File> fileData = FXCollections.observableArrayList();
    ObservableList<MySetting> settingsData = FXCollections.observableArrayList();

    Thread mainThread;
    Thread settingTread;
    Thread fileThread;

    private Connection conn;
    private Statement statementForFolder;
    private Statement statementForFile;
    private Statement statementForSettings;

    public DBConnect () {
        //������ ������ ������
        mainThread = new Thread(this);
        mainThread.start();
    }

    //������ ������������ � ����� ������
    @Override
    public void run() {
        //���������� � ��
        conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //�������� ����������� ������
        try {
            statementForFolder = conn.createStatement();
            statementForFile = conn.createStatement();
            statementForSettings = conn.createStatement();

            //�������� ������� �����
            statementForFolder.execute("CREATE TABLE Folder (id INTEGER PRIMARY KEY AUTOINCREMENT, Path STRING UNIQUE ON CONFLICT ABORTABORT);");

            //�������� ������� ������
            statementForFolder.execute("CREATE TABLE File (id INTEGER PRIMARY KEY AUTOINCREMENT, Path STRING UNIQUE ON CONFLICT IGNORE);");

            //�������� ������� ������ ��������
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

    //���������� ������ � �����
    void newDataOfFile (File file){
        String sql = "INSERT INTO File (Path) VALUES ('" + file.getPath() + "');";
        try {
            statementForFolder.execute(sql);
            fileData.add(file);
        } catch (SQLException e) {
            if(e.getErrorCode()!=19)
                e.printStackTrace();
        }
    }

    //�������� ������� � �����
    void delDataOfFile (File file){
        String sql = "DELETE FROM File WHERE Path='" + file.getPath() + "';";
        try {
            statementForFolder.execute(sql);
            fileData.remove(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //��������� ������� � �����
    void newDataOfFolder (File folder){
        String sql = "INSERT INTO Folder (Path) VALUES ('" + folder.getPath() + "');";
        try {
            statementForFolder.execute(sql);
            folderData.add(folder);
        } catch (SQLException e) {
            if(e.getErrorCode()!=19)
                e.printStackTrace();
        }
    }

    //�������� ������� � �����
    void delDataOfFolder (File file){
        String sql = "DELETE FROM Folder WHERE Path='" + file.getPath() + "';";
        try {
            statementForFolder.execute(sql);
            folderData.remove(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //�������� ������ ������
    private void CreateAListOfSettings (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Settings");
            while(resultSet.next())
                settingsData.add(new MySetting(resultSet.getInt("id"), resultSet.getString("Name"), resultSet.getString("Value")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //�������� ������ �����
    private void CreateAListOfFolder (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Folder");
            while(resultSet.next())
                folderData.add(new File(resultSet.getString("Path")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //�������� ������ �����
    private void CreateAListOfFile (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM File");
            while(resultSet.next())
                fileData.add(new File(resultSet.getString("Path")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //������� �������� ������
    ObservableList<MySetting> getDataOfSettings (){
        try {
            settingTread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return settingsData;
    }

    ObservableList<File> getDataOfFolder (){
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderData;
    }

    ObservableList<File> getDataOfFile (){
        try {
            fileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileData;
    }
}
