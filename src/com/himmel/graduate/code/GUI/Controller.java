package com.himmel.graduate.code.GUI;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.MyFolder;
import com.himmel.graduate.code.DB.Data.MySetting;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;

/**
 * Created by Lyaro on 19.12.2015.
 */
public class Controller  {
    public AnchorPane devices;
    public AnchorPane data;
    public AnchorPane time;
    public AnchorPane langyage;

    public TableView <MyFolder>dataTable;
    public TableColumn <MyFolder, String> dataColumn;

    private DBManagmnet db;
    private Stage stage;


    public void setDB (DBManagmnet DB){
        db = DB;
        dataTable.setItems(db.getDataOfFolder());
        dataColumn.setCellValueFactory(cellData -> cellData.getValue().pathProperty());
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void initialize (){

    }

    public void newData(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        db.newDataOfFolder(directoryChooser.showDialog(stage));
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
}
