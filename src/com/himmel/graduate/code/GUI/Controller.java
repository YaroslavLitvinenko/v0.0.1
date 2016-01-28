package com.himmel.graduate.code.GUI;

import com.himmel.graduate.code.DB.DBConnect;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Created by Lyaro on 19.12.2015.
 */
public class Controller {
    private static DBConnect db;
    public TextField test1;
    public AnchorPane devices;
    public AnchorPane data;
    public AnchorPane time;
    public AnchorPane langyage;

    public static void setDB (DBConnect DB){
        db = DB;
    }

    public void initialize (){

    }

    public void newData(ActionEvent actionEvent) {

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
