package com.ems.util;

import javax.swing.*;
import java.awt.*;

public final class UiInitializer {
    private UiInitializer() {}

    public static void init() {
        // Enable anti-aliased text
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Try to set Nimbus look and feel for a modern appearance; fallback to system LAF
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignore) {}
        }

        // Set consistent default fonts for common Swing components
        Font body = AppTheme.FONT_BODY;
        Font title = AppTheme.FONT_SUBTITLE;

        UIManager.put("Label.font", body);
        UIManager.put("Button.font", body);
        UIManager.put("ToggleButton.font", body);
        UIManager.put("RadioButton.font", body);
        UIManager.put("CheckBox.font", body);
        UIManager.put("TextField.font", body);
        UIManager.put("PasswordField.font", body);
        UIManager.put("TextArea.font", body);
        UIManager.put("Table.font", body);
        UIManager.put("TableHeader.font", title);
        UIManager.put("List.font", body);
        UIManager.put("ComboBox.font", body);
        UIManager.put("Menu.font", body);
        UIManager.put("MenuItem.font", body);
        UIManager.put("PopupMenu.font", body);
    }
}
