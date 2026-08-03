package com.ems.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerUtil {
    private static final Logger LOG = Logger.getLogger("ems");

    static {
        try {
            Path logs = Path.of("logs");
            if (!Files.exists(logs)) Files.createDirectories(logs);
            FileHandler fh = new FileHandler("logs/app.log", 10 * 1024 * 1024, 5, true);
            fh.setFormatter(new SimpleFormatter());
            LOG.addHandler(fh);
            LOG.setLevel(Level.INFO);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to initialize file logging", e);
        }
    }

    private LoggerUtil() {}

    public static void info(String msg) { LOG.info(msg); }
    public static void warn(String msg) { LOG.warning(msg); }
    public static void error(String msg, Throwable t) { LOG.log(Level.SEVERE, msg, t); }
}
