package com.himmel.graduate.code.Management;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.GUI.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

//Стандартный GUI JavaFX
public class Main extends Application {
    private final String APPLICATIO_NAME = "Graduate v0.0.1";
    private final DBManagmnet db = new DBManagmnet();
    private Stage stage;
    private Controller mainController;

    public static void main(String[] args) throws InterruptedException {
        new Manager();
        //launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        //Позволяет не выходить при закрытии окна
        Platform.setImplicitExit(false);

        //Класс для работы с треем
        new WorkingWithTray(this, APPLICATIO_NAME).addAplicationToTray();

        //Инициализация GUI
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/himmel/graduate/code/GUI/sample.fxml"));
        primaryStage.setScene(new Scene(mainLoader.load(), 500, 300));
        mainController = mainLoader.getController();
        mainController.setDB(db);
        mainController.setStage(primaryStage);
        primaryStage.setTitle(APPLICATIO_NAME);
        primaryStage.getIcons().add(new Image("com/himmel/graduate/images/image.png"));
        showStage();

    }

    //Позволяет правильно открывать окна
    public void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
}
