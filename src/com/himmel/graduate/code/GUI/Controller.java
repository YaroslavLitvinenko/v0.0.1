package com.himmel.graduate.code.GUI;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.DB.Data.MySetting;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Lyaro on 19.12.2015.
 */
public class Controller  {
    public AnchorPane devices;
    public AnchorPane data;
    public AnchorPane time;
    public AnchorPane langyage;
    public TextField pathToFile;


    private DBManagmnet db;
    private Stage stage;

    private MessageBox messageBox;


    public void setDB (DBManagmnet DB){
        db = DB;
        pathToFile.setText(db.getDataOfSettings().get(0).getValue());
    }

    public void setStage (Stage stage) {
        this.stage = stage;
    }

    public void initialize (){
        messageBox = new MessageBox();
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
                file = new File(file.getPath() + "\\test.txt");
                if (file.createNewFile()) {
                    file.delete();
                    file = new File(file.getPath().substring(0, (file.getPath().length() - 9)));
                    db.setSetting(new MySetting(0, "mainPages", file.getPath()));
                    pathToFile.setText(db.getDataOfSettings().get(0).getValue());
                } else {
                    messageBox.showMassage(MessageBox.Typ.ERROR, "Данная папка недоступна для чтения/записи!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
