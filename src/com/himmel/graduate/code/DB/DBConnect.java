package com.himmel.graduate.code.DB;

import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.DB.Data.MyFolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * Created by Lyaro on 20.12.2015.
 */
public class DBConnect implements Runnable {
    private Connection conn;
    private Statement statementForFile;
    private Statement statementForFolder;
    private ResultSet resSet;

    private ObservableList<MyFile> fileData = FXCollections.observableArrayList();
    private ObservableList<MyFolder> folderData = FXCollections.observableArrayList();

    public DBConnect () {
        //Запуск нового потока
        new Thread(this).start();
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
            statementForFile = conn.createStatement();
            statementForFolder = conn.createStatement();

            //Создание таблицы файлов
            statementForFile.execute("CREATE TABLE if not exists 'File' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Path' STRING, 'md5' STRING);");

            //Создание таблицы папок
            statementForFolder.execute("CREATE TABLE if not exists 'Folder' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Path' STRING, 'md5' STRING);");
        } catch (SQLException e) {
            System.err.println(e);
        }

        new Thread(()->this.CreateAListOfFiles(statementForFile)).start();
        CreateAListOfFolder(statementForFolder);
    }

    //Создание списка файлов
    private void CreateAListOfFiles (Statement statement){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM File");
            while(resultSet.next())
                fileData.add(new MyFile (resultSet.getInt("id"), resultSet.getString("Path"), resultSet.getString("md5")));
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
}
