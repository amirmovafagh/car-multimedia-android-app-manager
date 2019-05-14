package ir.dev_roid.testusb.app;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class Logger {

    public static final String APP_ID = "Multi Media";
    public static String logDir = "/multimedia";
    public static String logFileName = "/log.txt";
    public static boolean writeLogsToFile = true;
    public static final int LOG_LEVEL_VERBOSE = 4;
    public static final int LOG_LEVEL_DEBUG = 3;
    public static final int LOG_LEVEL_INFO = 2;
    public static final int LOG_LEVEL_ERROR = 1;
    public static final int LOG_LEVEL_OFF = 0;
    public static final int CURRENT_LOG_LEVEL = LOG_LEVEL_DEBUG;

    public static void log(String message, int logLevel) {
        if (logLevel > CURRENT_LOG_LEVEL) {
            return;
        } else {
            Log.v(APP_ID, message);
            if (writeLogsToFile) {
                writeToFile(message);
            }
        }
    }

    private static void writeToFile(String message) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + logDir);
            dir.mkdirs();
            File file = new File(dir, logFileName);
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true), 8 * 1024));
            writer.println(APP_ID + " " + new Date().toString() + " : " + message);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verbose(String message) {
        log(message, LOG_LEVEL_VERBOSE);
    }

    public static void debug(String message) {
        log(message, LOG_LEVEL_DEBUG);
    }

    public static void error(String message) {
        log(message, LOG_LEVEL_ERROR);
    }

    public void info(String message) {
        log(message, LOG_LEVEL_INFO);
    }
}