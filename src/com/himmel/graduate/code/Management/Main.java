package com.himmel.graduate.code.Management;

import com.himmel.graduate.code.DB.DBManagmnet;
import com.himmel.graduate.code.GUI.Controller;
import com.himmel.graduate.code.Network.Connect;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

//Стандартный GUI JavaFX
public class Main extends Application {
    public static final DBManagmnet db = new DBManagmnet();
    private final String APPLICATIO_NAME = "Graduate v0.0.1";
    private Stage stage;
    private Controller mainController;
    private Manager manager;

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
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/himmel/graduate/code/GUI/sample.fxml"));
        primaryStage.setScene(new Scene(mainLoader.load()));
        mainController = mainLoader.getController();
        manager = new Manager(db, mainController);
        mainController.setDB(db);
        mainController.setManager(manager);
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
