package com.cuelogic.firebase.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefUtil {
    private static final String PREF_NAME = "CueConnectPrefs";
    private static final int MODE = Context.MODE_PRIVATE;

    private static final String IS_NOTIFICATIONS_ENABLED = "IS_NOTIFICATIONS_ENABLED"; //boolean
    private static final String FIREBASE_TOKEN = "FIREBASE_TOKEN"; //String

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }
    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void clearAllPrefs(Context context) {
        getEditor(context).clear().commit();
    }

    public static boolean isNotificationsEnabled(Context context) {
        return getPreferences(context).getBoolean(IS_NOTIFICATIONS_ENABLED, true);
    }
    public static void setNotificationsEnabled(Context context, boolean notificationsEnabled) {
        getEditor(context).putBoolean(IS_NOTIFICATIONS_ENABLED, notificationsEnabled).commit();
    }

    public static String getFirebaseToken(Context context) {
        return getPreferences(context).getString(FIREBASE_TOKEN, "");
    }
    public static void setFirebaseToken(Context context, String firebaseToken) {
        getEditor(context).putString(FIREBASE_TOKEN, firebaseToken).commit();
    }
}
