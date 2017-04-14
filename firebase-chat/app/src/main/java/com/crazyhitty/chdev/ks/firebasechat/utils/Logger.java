package com.crazyhitty.chdev.ks.firebasechat.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is for debugging purpose, is should manage with "isDebug" flag.
 *
 * @author Cuelogic
 * @version 1.0.0
 * @Date 11/12/2015
 */
public class Logger {

    private static final String TAG = Logger.class.getSimpleName();

    public static void vLog(String contextTag, String logMessage) {
        if (Constants.IS_APP_DEBUG)
            Log.v(Constants.APP_NAME, contextTag + " : " + logMessage);
    }

    public static void vLog(String contextTag, String logMessage, boolean isTimeStamp) {
        String timestamp = isTimeStamp ? " : " + System.currentTimeMillis() : "";
        if (Constants.IS_APP_DEBUG)
            Log.v(Constants.APP_NAME, contextTag + " : " + logMessage + timestamp);
    }

    /**
     * @param contextTag
     * @param logMessage
     */
    public static void wLog(String contextTag, String logMessage) {
        if (Constants.IS_APP_DEBUG)
            Log.w(Constants.APP_NAME, contextTag + " : " + logMessage);
    }

    /**
     * @param contextTag
     * @param logMessage
     */
    public static void eLog(String contextTag, String logMessage) {
        if (Constants.IS_APP_DEBUG)
            Log.e(Constants.APP_NAME, contextTag + " : " + logMessage);
    }

//    /**
//     * @param logMessage
//     */
//    public static void vLog(String contextTag, String logMessage) {
//        if (Constants.IS_DEBUG)
//            System.out.println(contextTag + " : " + logMessage);
//    }

    /**
     * <uses-permission android:name="android.permission.READ_LOGS" />
     *
     * @author Swapnil Sonar
     * @Date 07/01/2015
     */
    public static void saveLogsToSDCard() {

        try {
            // logcat -d
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            appendLog(log.toString());
            Logger.vLog(TAG, "saveLogsToSDCard: " + log.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     *
     * @param text
     */
    public static void appendLog(String text) {
        Logger.vLog(TAG, "appendLog: " + text);

        File logFile = new File("sdcard/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
