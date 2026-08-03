package com.ems;

import com.ems.ui.LoginFrame;
import com.ems.ui.SplashScreen;
import com.ems.util.UiInitializer;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiInitializer.init();
            SplashScreen splash = new SplashScreen();
            splash.playAndThen(() -> {
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            });
        });
    }
}
