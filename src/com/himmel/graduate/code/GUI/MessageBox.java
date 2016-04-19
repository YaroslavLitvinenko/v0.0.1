package com.himmel.graduate.code.GUI;

import javafx.scene.control.Alert;

/**
 * Created by Lyaro on 19.04.2016.
 */
public class MessageBox {
    private static final Alert windowForMassage = new Alert(Alert.AlertType.INFORMATION);

    public MessageBox() {
        windowForMassage.setHeaderText(null);
    }

    public void showMassage (Typ typ, String message) {
        if (typ == Typ.INFORMATION) {
            windowForMassage.setAlertType(Alert.AlertType.INFORMATION);
            windowForMassage.setTitle("Information");
            windowForMassage.setContentText(message);
        } else {
            windowForMassage.setAlertType(Alert.AlertType.ERROR);
            windowForMassage.setTitle("Error");
            windowForMassage.setContentText(message);
        }
        windowForMassage.show();
    }

    public enum Typ {
        INFORMATION,
        ERROR;
    }

}
