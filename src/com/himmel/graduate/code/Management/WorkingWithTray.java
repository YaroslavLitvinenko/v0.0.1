package com.himmel.graduate.code.Management;

import javafx.application.Platform;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Lyaro on 19.12.2015.
 */

//���� ��� ������ � �����
public class WorkingWithTray {
    //����� �� Main ����������� ��������� ��������� ����
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
            //������������� ������, ����� ��� ������-���
            java.awt.Toolkit.getDefaultToolkit();

            //������� ������� �������
            if (!java.awt.SystemTray.isSupported()) {
                System.err.println("No system tray support, application exiting.");
                Platform.exit();
            }

            //��������� ���������� ����
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

            //��������� ��������
            //�������� ������ ��� ����
            URL imageLoc = Main.class.getResource("/com/himmel/graduate/images/image.png");
            java.awt.Image image = ImageIO.read(imageLoc);
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            trayIcon.setImageAutoSize(true);

            //��������� �������� ������� �� ������(��������� ����)
            trayIcon.addActionListener(event -> Platform.runLater(main::showStage));

            //���� ������ ������ �������� ����
            java.awt.MenuItem openItem = new java.awt.MenuItem("Show window");
            openItem.addActionListener(event -> Platform.runLater(main::showStage));

            //����� �� ���������� ��� ������� �� �����
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);//TODO c������ ���������� �����
            });

            //�������� ����������� ����
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            //���������� � ���� ������ ������ ����
            popup.add(openItem);
            //���������� � ���� �����������
            popup.addSeparator();
            //���������� � ���� ������ ������
            popup.add(exitItem);
            //������������ ���� � ������ � ����
            trayIcon.setPopupMenu(popup);

            //����� ��������� � ������� ����������
            javax.swing.SwingUtilities.invokeLater(() ->trayIcon.displayMessage(APPLICATIO_NAME, "Synchronization is run", java.awt.TrayIcon.MessageType.INFO));

            //���������� ������ � ����
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }
}
