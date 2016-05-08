package com.himmel.graduate.code.GUI;

import javafx.scene.control.Alert;

/**
 * Created by Lyaro on 19.04.2016.
 */
public class MessageBox {
    //TODO Переделать в обычное окно для пременения
    private static final Alert WINDOW_FOR_MASSAGE;
    static {
        WINDOW_FOR_MASSAGE = new Alert(Alert.AlertType.INFORMATION);
        WINDOW_FOR_MASSAGE.setHeaderText(null);
    }

    private MessageBox() { }

    public static void showMassage (Typ typ, String message) {
        if (typ == Typ.INFORMATION) {
            WINDOW_FOR_MASSAGE.setAlertType(Alert.AlertType.INFORMATION);
            WINDOW_FOR_MASSAGE.setTitle("Information");
            WINDOW_FOR_MASSAGE.setContentText(message);
        } else {
            WINDOW_FOR_MASSAGE.setAlertType(Alert.AlertType.ERROR);
            WINDOW_FOR_MASSAGE.setTitle("Error");
            WINDOW_FOR_MASSAGE.setContentText(message);
        }
        WINDOW_FOR_MASSAGE.show();
    }

    public enum Typ {
        INFORMATION,
        ERROR;
    }

}
