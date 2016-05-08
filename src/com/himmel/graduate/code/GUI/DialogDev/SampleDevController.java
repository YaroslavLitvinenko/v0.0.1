package com.himmel.graduate.code.GUI.DialogDev;

import com.himmel.graduate.code.DB.DBManagmnet;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Lyaro on 07.05.2016.
 */
// онтроллер дл€ диалога добавлени€ устройства
public class SampleDevController {
    public TextField mac;
    Stage stage;
    DBManagmnet db;

    public void setDB(DBManagmnet db) {
        this.db = db;
    }

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void ok(ActionEvent actionEvent) {
        if (mac.getText() != null || mac.getText() != "")
            db.newDataOfDevices(mac.getText());
        stage.close();
    }

    public void cancel(ActionEvent actionEvent) {
        stage.close();
    }
}
