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
    ObservableList<MyFile> myFileData = FXCollections.observableArrayList();
    ObservableList<String> deviceData = FXCollections.observableArrayList();

    Thread mainThread;
    Thread settingTread;
    Thread fileThread;
    Thread otherThread;

    private Connection conn;
    private Statement statementForFolder = null;
    private Statement statementForFile = null;
    private Statement statementForSettings = null;
    private Statement statementForOther = null;

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
            statementForOther = conn.createStatement();

            //Создание таблицы папок
            statementForFolder.execute("CREATE TABLE IF NOT EXISTS Folder (id INTEGER PRIMARY KEY AUTOINCREMENT, Path STRING UNIQUE ON CONFLICT ABORT);");

            //Создание таблицы файлов
            statementForFolder.execute("CREATE TABLE if not exists 'File' (id INTEGER PRIMARY KEY AUTOINCREMENT, Path STRING UNIQUE ON CONFLICT ABORT, dateLastChanges STRING (20), dateHash STRING (20));");

            //Создание таблицы списка настроек
            statementForSettings.execute("CREATE TABLE if not exists 'Settings' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'Name' STRING UNIQUE ON CONFLICT ABORT, 'Value' STRING);");
            ResultSet resultSet = statementForSettings.executeQuery("SELECT * FROM Settings;");
            if (!resultSet.next()) {
                statementForSettings.execute("INSERT INTO 'Settings' ('Name', 'Value') VALUES ('mainPages', '');");
                statementForSettings.execute("INSERT INTO 'Settings' ('Name', 'Value') VALUES ('language', '2');");
            }

            //Создане всех остальных таблиц
            //Создание таблицы устройств для синхронизации
            statementForOther.execute("CREATE TABLE IF NOT EXISTS 'DeviceForSync' (id INTEGER PRIMARY KEY AUTOINCREMENT, MAC STRING UNIQUE ON CONFLICT ABORT);");
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            try {
                if (statementForFolder != null)
                    statementForFolder.close();
                if (statementForFile != null)
                    statementForFile.close();
                if (statementForSettings != null)
                    statementForSettings.close();
                if (statementForOther != null)
                    statementForOther.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        settingTread = new Thread(() -> this.CreateAListOfSettings(statementForSettings));
        settingTread.start();
        fileThread = new Thread(() -> this.CreateAListOfFiles(statementForFile));
        fileThread.start();
        otherThread = new Thread(() -> this.CreateOtherList(statementForOther));
        otherThread.start();
        CreateAListOfFolders(statementForFolder);
    }

    //Добавление данных о файле
    void newDataOfFile(File file) {
        File buf = new File (settingsData.get(0).getValue() + File.separator + file.getPath());
        newDataOfFile(new MyFile(0, file.getPath(), MyFile.SIMPLE_DATE_FORMAT.format(new java.util.Date(buf.lastModified())),
                MyFile.SIMPLE_DATE_FORMAT.format(new java.util.Date(buf.lastModified()))));
    }

    //Добавление данных о файле
    void newDataOfFile(MyFile file) {
        try {
            ResultSet resultSet = statementForFile.executeQuery("SELECT * FROM File WHERE Path = '" + file.getPath() + "';");
            MyFile buf = new MyFile(file);
            buf.setPath(settingsData.get(0).getValue() + File.separator + file.getPath());
            String sql = "";
            if (resultSet.next()){
                if (!resultSet.getString("dateLastChanges").equals(MyFile.SIMPLE_DATE_FORMAT.format(buf.getDateLastChanges()))) {
                    sql = "UPDATE File SET dateLastChanges='" +  MyFile.SIMPLE_DATE_FORMAT.format(buf.getDateLastChanges());
                    sql+="', dateHash='";
                    sql+= MyFile.SIMPLE_DATE_FORMAT.format(buf.getDateHash());
                    sql+="' WHERE Path='" + file.getPath() + "';";
                    buf = myFileData.get(myFileData.indexOf(file));
                    buf.setDateLastChanges(file.getDateLastChanges());
                    buf.setDateHash(file.getDateHash());
                } else return;
            } else {
                sql = "INSERT INTO File (Path, dateLastChanges, dateHash) VALUES ('" + file.getPath() + "', '";
                sql+= MyFile.SIMPLE_DATE_FORMAT.format(buf.getDateLastChanges());
                sql+="', '";
                sql+=MyFile.SIMPLE_DATE_FORMAT.format(buf.getDateHash());
                sql+="');";

                fileData.add(new File (file.getPath()));
                myFileData.add(file);
            }
            statementForFolder.execute(sql);
        } catch (SQLException e) {
            if(e.getErrorCode()!=19)
                e.printStackTrace();
        }
    }

    //Удаление даннных о файле
    void delDataOfFile(File file) {
        String sql = "DELETE FROM File WHERE Path='" + file.getPath() + "';";
        try {
            statementForFolder.execute(sql);
            for (int i = 0; i < fileData.size(); i++) {
                if (fileData.get(i).getPath().equals(file.getPath())) {
                    fileData.remove(i);
                    myFileData.remove(i);
                }
            }
            fileData.remove(file);
            myFileData.remove(new MyFile(file.getPath()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Добавлене даннных о папке
    void newDataOfFolder(File folder) {
        String sql = "INSERT INTO Folder (Path) VALUES ('" + folder.getPath() + "');";
        try {
            statementForFolder.execute(sql);
            folderData.add(folder);
        } catch (SQLException e) {
            if(e.getErrorCode()!=19)
                e.printStackTrace();
        }
    }

    //Удfление даннных о папке
    void delDataOfFolder(File folder) {
        String sql = "DELETE FROM Folder WHERE Path='" + folder.getPath() + "';";
        try {
            statementForFolder.execute(sql);
            for (int i = 0; i < folderData.size(); i++)
                if (fileData.get(i).getPath().equals(folder.getPath()))
                    folderData.remove(i);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        folderData.remove(folder);
    }

    //Добавление данных об устройстве
    void newDataOfDevices(String mac) {
        String sql = "INSERT INTO DeviceForSync (MAC) VALUES ('" + mac + "');";
        try {
            statementForOther.execute(sql);
            deviceData.add(mac);
        } catch (SQLException e) {
            if(e.getErrorCode()!=19)
                e.printStackTrace();
        }
    }

    //Удаление данных о девайсе
    void delDataOfDvices(String mac) {
        String sql = "DELETE FROM DeviceForSync WHERE MAC='" + mac + "';";
        try {
            statementForFolder.execute(sql);/*
            for (int i = 0; i < deviceData.size(); i++)
                if (deviceData.get(i).equals(mac))
                    deviceData.remove(i);*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
        deviceData.remove(mac);
    }

    //Создание списка файлов
    private void CreateAListOfSettings(Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Settings");
            while(resultSet.next())
                settingsData.add(new MySetting(resultSet.getInt("id"), resultSet.getString("Name"), resultSet.getString("Value")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Создание списка папок
    private void CreateAListOfFolders(Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Folder");
            while(resultSet.next())
                folderData.add(new File(resultSet.getString("Path")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Создание списка файлов
    private void CreateAListOfFiles(Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM File");
            while (resultSet.next()) {
                fileData.add(new File(resultSet.getString("Path")));
                myFileData.add(new MyFile(resultSet.getInt("id"), resultSet.getString("Path"), resultSet.getString("dateLastChanges"), resultSet.getString("dateHash")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void CreateOtherList(Statement statement) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM DeviceForSync");
            while (resultSet.next()) {
                deviceData.add(resultSet.getString("MAC"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Возрат значение из списка, по данным о пути файла
    MyFile getDataOfFile(MyFile file) {
        return myFileData.get(myFileData.indexOf(file));
    }

    void setSetting (MySetting setting) {
        try {
            String sql = "UPDATE Settings SET Value='" +  setting.getValue();
            sql+="' WHERE Name='" + setting.getName() + "';";
            statementForFolder.execute(sql);
            settingsData.get(setting.getId()).setValue(setting.getValue());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Возврат значений таблиц
    ObservableList<MySetting> getDataOfSettings (){
        try {
            settingTread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return settingsData;
    }

    ObservableList<File> getDataOfFolders (){
        try {
            mainThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return folderData;
    }

    ObservableList<File> getDataOfFiles (){
        try {
            fileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fileData;
    }

    ObservableList<MyFile> getDataOfMyFiles() {
        try {
            fileThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myFileData;
    }

    ObservableList<String> getDataOfDevice() {
        try {
            otherThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return deviceData;
    }
}
