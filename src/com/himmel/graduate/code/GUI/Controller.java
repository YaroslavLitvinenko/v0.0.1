package com.himmel.graduate.code.GUI;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.MyFile;
import com.himmel.graduate.code.DB.Data.MySetting;
import com.himmel.graduate.code.GUI.DialogDev.SampleDevController;
import com.himmel.graduate.code.Management.Main;
import com.himmel.graduate.code.Management.Manager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lyaro on 19.12.2015.
 */
public class Controller  {
    public static final String [][] LANGUAGE = new String[][] {{"Главная", "Настройка", "О программе", "Устройства", "Папки", "Время", "Язык", "Вывод", "Устройства для синхронизации", "Добавить", "Удалить", "Выбрать другую ппку", "Ввод времени синхронизации", "Ввод времени", " час", " часа", " часов", "Выбор языка:", "MAC-адрес: "},
                                                  {"Головна", "Налаштування", "Про программу", "Пристрої", "Папки", "Час","Мова", "Виведення", "Пристрої для синхронізації", "Добавити", "Видалити", "Обрати іншу папку", "Введення часу синхронізації", "Введення часу", " година", " години", " годин", "Вибір мови:", "MAC-адреca: "},
                                                  {"Main page", "Settings", "About the program", "Devices", "Folders", "Time", "Language", "Output", "To synchronize your device", "Add", "Delete", "Select another folder", "Entering time synchronization", "Entering time", " hour", " hours", " hours", "Language Selection:", "MAC: "}};
    public Tab mainPage;
    public Tab settingsPage;
    public Tab aboutTheProgrammePage;
    public Button butDevicws;
    public Button butFolders;
    public Button butTime;
    public Button butLanguage;
    //Список устройств обновляется в классе Manager
    public TableView<String> devicesTable;
    public TableColumn<String, String> devicesColumn;
    public ObservableList<String> listDevice = FXCollections.observableArrayList();
    public Button butAddDevice;
    public Button btDelDevice;
    public Button butAddNewFolder;
    public Label labelTime;
    public Label labelTime2;
    public RadioButton hour1;
    public RadioButton hour2;
    public RadioButton hour5;
    public RadioButton hour24;
    public Label lableLanguage;

    public RadioButton eng;
    public RadioButton uk;
    public RadioButton rus;
    public AnchorPane devices;
    public AnchorPane data;
    public AnchorPane time;
    public AnchorPane langyage;
    public TextField pathToFile;
    public Label mac;

    //TODO нихрена он не обновляется
    //Список файлов обновляется в классе FileManager
    public TableView<MyFile> tableOut;
    public TableColumn<MyFile, String> columnOut;
    public ObservableList<MyFile> listOut = FXCollections.observableArrayList();

    //Прогрес бар изменяется в классе Client
    public ProgressBar progres ;
    private String nayMac;
    private int numberLanguage;
    private DBManagmnet db;
    private Stage stage;

    private MessageBox messageBox;
    private Manager manager;


    public void setDB (DBManagmnet DB){
        db = DB;
        pathToFile.setText(db.getDataOfSettings().get(0).getValue());
        numberLanguage = Integer.parseInt(db.getDataOfSettings().get(1).getValue());
        switch (numberLanguage) {
            case 0:
                rus.setSelected(true);
                break;
            case 1:
                uk.setSelected(true);
                break;
            case 2:
                eng.setSelected(true);
                break;
        }
        updateLanguage();
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        messageBox = new MessageBox();
        tableOut.setItems(Main.db.getDataOfMyFiles());
        columnOut.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
        devicesTable.setItems(Main.db.getDataOfDevice());
        devicesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
        devicesTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showData(newValue));
    }

    private void showData (String mac){
        nayMac = mac;
    }


    public void deviceAction(ActionEvent actionEvent) {
        devices.setVisible(true);
        data.setVisible(false);
        time.setVisible(false);
        langyage.setVisible(false);
    }

    public void dataAction(ActionEvent actionEvent) {
        devices.setVisible(false);
        data.setVisible(true);
        time.setVisible(false);
        langyage.setVisible(false);
    }

    public void timeAction(ActionEvent actionEvent) {
        devices.setVisible(false);
        data.setVisible(false);
        time.setVisible(true);
        langyage.setVisible(false);
    }

    public void languageAction(ActionEvent actionEvent) {
        devices.setVisible(false);
        data.setVisible(false);
        time.setVisible(false);
        langyage.setVisible(true);
    }


    public void newFolderOfSync(ActionEvent actionEvent) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                file = new File(file.getPath() + File.separator + "test.txt");
                if (file.createNewFile()) {
                    file.delete();
                    file = new File(file.getPath().substring(0, (file.getPath().length() - 9)));

                    manager.stopSync();

                    db.setSetting(new MySetting(0, "mainPages", file.getPath()));
                    pathToFile.setText(db.getDataOfSettings().get(0).getValue());

                    manager.startSync();
                } else {
                    messageBox.showMassage(MessageBox.Typ.ERROR, "Данная папка недоступна для чтения/записи!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }


    public void selectEnglish(ActionEvent actionEvent) {
        numberLanguage = 2;
        updateLanguage();
        db.setSetting(new MySetting(1, "language", "2"));
    }

    public void selectRussen(ActionEvent actionEvent) {
        numberLanguage = 0;
        updateLanguage();
        db.setSetting(new MySetting(1, "language", "0"));
    }

    public void selectUkraine(ActionEvent actionEvent) {
        numberLanguage = 1;
        updateLanguage();
        db.setSetting(new MySetting(1, "language", "1"));
    }


    private void updateLanguage() {
        mainPage.setText(LANGUAGE[numberLanguage][0]);
        settingsPage.setText(LANGUAGE[numberLanguage][1]);
        aboutTheProgrammePage.setText(LANGUAGE[numberLanguage][2]);
        butDevicws.setText(LANGUAGE[numberLanguage][3]);
        butFolders.setText(LANGUAGE[numberLanguage][4]);
        butTime.setText(LANGUAGE[numberLanguage][5]);
        butLanguage.setText(LANGUAGE[numberLanguage][6]);
        columnOut.setText(LANGUAGE[numberLanguage][7]);
        devicesColumn.setText(LANGUAGE[numberLanguage][8]);
        butAddDevice.setText(LANGUAGE[numberLanguage][9]);
        btDelDevice.setText(LANGUAGE[numberLanguage][10]);
        butAddNewFolder.setText(LANGUAGE[numberLanguage][11]);
        labelTime.setText(LANGUAGE[numberLanguage][12]);
        labelTime2.setText(LANGUAGE[numberLanguage][13]);
        hour1.setText(1 + LANGUAGE[numberLanguage][14]);
        hour2.setText(2 + LANGUAGE[numberLanguage][15]);
        hour5.setText(5 + LANGUAGE[numberLanguage][16]);
        hour24.setText(24 + LANGUAGE[numberLanguage][15]);
        lableLanguage.setText(LANGUAGE[numberLanguage][17]);
        mac.setText(LANGUAGE[numberLanguage][18] + com.himmel.graduate.code.Network.Connect.MAC);
    }

    public void addDevice(ActionEvent actionEvent) {
        //TODO Допилить языки в диалогах
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Controller.class.getResource("/com/himmel/graduate/code/GUI/DialogDev/sampleDev.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add mac");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.getIcons().add(new Image("com/himmel/graduate/images/image.png"));

            // Set the person into the controller.
            SampleDevController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDB(db);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delDevice(ActionEvent actionEvent) {
        if (nayMac != null) {
            Main.db.delDataOfDvices(nayMac);
        }
    }
}
