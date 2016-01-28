package com.himmel.graduate.code.Management;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Lyaro on 19.12.2015.
 */

//Клас для работы с треем
public class WorkingWithTray {
    //Сылка на Main позволяющая открывать закрывать окно
    private Main main;
    private String APPLICATIO_NAME;

    public WorkingWithTray (Main main, String appName){
        this.main = main;
        APPLICATIO_NAME = appName;
    }

    public void addAplicationToTray (){
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
    }

    private void addAppToTray() {
        try {
            //Инициализация тулкит, вроде как тулкит-бар
            java.awt.Toolkit.getDefaultToolkit();

            //Провека наличия тулбара
            if (!java.awt.SystemTray.isSupported()) {
                System.err.println("No system tray support, application exiting.");
                Platform.exit();
            }

            //Получение системного трея
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

            //Получение картинки
            //Создание иконки для трея
            URL imageLoc = Main.class.getResource("/com/himmel/graduate/images/image.png");
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            trayIcon.setImageAutoSize(true);

            //Обработка двойного нажатия на иконку(открытияе окна)
            trayIcon.addActionListener(event -> Platform.runLater(main::showStage));

            //Если нажата ктопка открытия окна
            java.awt.MenuItem openItem = new java.awt.MenuItem("Show window");
            openItem.addActionListener(event -> Platform.runLater(main::showStage));

            //Выход из приложения при нажатии на выход
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);//TODO cделать нормальный выход
            });

            //создание всплывающео меню
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            //Добавление в меню пункта показа окна
            popup.add(openItem);
            //Добавление в меню разделителя
            popup.addSeparator();
            //Добавление в меню пункта выхход
            popup.add(exitItem);
            //Прикрепление меню к иконке в трее
            trayIcon.setPopupMenu(popup);

            //Вывод сообщения о запуске приложения
            javax.swing.SwingUtilities.invokeLater(() ->trayIcon.displayMessage(APPLICATIO_NAME, "Synchronization is run", java.awt.TrayIcon.MessageType.INFO));

            //Добавление иконки в трей
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }
}
