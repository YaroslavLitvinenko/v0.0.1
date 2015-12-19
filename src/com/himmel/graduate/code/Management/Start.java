package com.himmel.graduate.code.Management;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.*;

public class Start extends Application {
    private static final String APPLICATION_NAME = "Graduate v0.0.1";

    public static void main(String[] args) {
        WorkingWithTray workingWithTray = new WorkingWithTray(APPLICATION_NAME);
        workingWithTray.setApplicationWithTray();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/himmel/graduate/code/GUI/sample.fxml"));
        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }
}
