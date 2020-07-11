package com.emwit.remoteupdateapp;

import android.util.Log;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import java.io.IOException;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Log4j1Util {
    private static Logger infoLogger;
    private static Logger errorLogger;

    public static void init() {
        LogConfigurator logConfigurator = new LogConfigurator();

        /*  configura looger for debug message */
        logConfigurator.setRootLevel(Level.DEBUG);

        /* crate a logger for log info message */
        logConfigurator.setLevel("infoLogger", Level.DEBUG);
        Logger logger = Logger.getLogger("infoLogger");
        Layout fileLayout = new PatternLayout("%d{yyy MMM dd HH:mm:ss} %-5p %m%n");
        RollingFileAppender rollingFileAppender;
        try {
            rollingFileAppender = new RollingFileAppender(fileLayout, "/sdcard/remoteupdateapp/logs/info.log");
        } catch (IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }
        rollingFileAppender.setMaxBackupIndex(10);
        rollingFileAppender.setMaximumFileSize(1024 * 10);
        rollingFileAppender.setImmediateFlush(true);
        logger.addAppender(rollingFileAppender);

        /* crate a logger for log error message */
        logConfigurator.setLevel("errorLogger", Level.ERROR);
        Logger logger1 = Logger.getLogger("errorLogger");
        Layout fileLayout1 = new PatternLayout("%d{yyy MMM dd HH:mm:ss} %-5p %m%n");
        RollingFileAppender rollingFileAppender1;
        try {
            rollingFileAppender1 = new RollingFileAppender(fileLayout1, "/sdcard/remoteupdateapp/logs/error.log");
        } catch (IOException e) {
            throw new RuntimeException("Exception configuring log system", e);
        }

        rollingFileAppender1.setMaxBackupIndex(5);
        rollingFileAppender1.setMaximumFileSize(1024 * 10);
        rollingFileAppender1.setImmediateFlush(true);

        logger1.addAppender(rollingFileAppender1);

        infoLogger = Logger.getLogger("infoLogger");
        errorLogger = Logger.getLogger("errorLogger");
    }

    public static void i(String tag, String message) {
        infoLogger.info("[" + tag + "] " + message);
        // If need to show log to console, add this
        Log.i(tag, message);
    }

    public static void e(String tag, String message) {
        errorLogger.error("[" + tag + "] " + message);
        // If need to show log to console, add this
        Log.e(tag, message);
    }
}
