package com.ems.util;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class SubjectCatalog {
    private static final List<Runnable> LISTENERS = new CopyOnWriteArrayList<>();

    private SubjectCatalog() {
    }

    public static void addListener(Runnable listener) {
        if (listener != null) {
            LISTENERS.add(listener);
        }
    }

    public static void removeListener(Runnable listener) {
        LISTENERS.remove(listener);
    }

    public static void notifyChanged() {
        for (Runnable listener : LISTENERS) {
            SwingUtilities.invokeLater(listener);
        }
    }
}
