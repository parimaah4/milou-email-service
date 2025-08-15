package com.milou.util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerConfig {
    public static void setupLogger() {
        try {
            Logger logger = Logger.getLogger("");
            FileHandler fh = new FileHandler("log.txt", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}