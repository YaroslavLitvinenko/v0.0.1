package com.himmel.graduate.code.Management;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Created by Lyaro on 19.12.2015.
 */
public class WorkingWithTray {
    private final static String ICON_STR = "/com/himmel/graduate/images/image.png";
    private static String APPLICATION_NAME;

    public WorkingWithTray(String applicationName){
        APPLICATION_NAME = new String(applicationName);
    }

    public void setApplicationWithTray () {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setTrayIcon();
            }
        });
    }

    private void setTrayIcon() {

        if(! SystemTray.isSupported() ) {
            return;
        }

        PopupMenu trayMenu = new PopupMenu();
        MenuItem item = new MenuItem("Exit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayMenu.add(item);

        URL imageURL = Start.class.getResource(ICON_STR);

        Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
        TrayIcon trayIcon = new TrayIcon(icon, APPLICATION_NAME, trayMenu);
        trayIcon.setImageAutoSize(true);

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.displayMessage(APPLICATION_NAME, "Application started!", TrayIcon.MessageType.INFO);
    }
}
