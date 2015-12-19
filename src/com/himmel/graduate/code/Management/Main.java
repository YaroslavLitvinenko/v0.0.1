package com.himmel.graduate.code.Management;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

//Стандартный GUI JavaFX
public class Main extends Application {
    private final String APPLICATIO_NAME = "Graduate v0.0.1";
    private Stage stage;

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        //Позволяет не выходить при закрытии окна
        Platform.setImplicitExit(false);

        //Класс для работы с треем
        new WorkingWithTray(this, APPLICATIO_NAME).addAplicationToTray();

        //Инициализация GUI
        Parent root = FXMLLoader.load(getClass().getResource("/com/himmel/graduate/code/GUI/sample.fxml"));
        primaryStage.setTitle(APPLICATIO_NAME);
        primaryStage.setScene(new Scene(root, 400, 300));
    }

    //Позволяет правильно открывать окна
    public void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
}
