package com.cuelogic.firebase.chat;

import android.app.Application;
import android.content.Context;

import com.cuelogic.firebase.chat.database.DatabaseHelper;

//http://www.crazyhitty.com/one-to-one-chat-using-firebase-for-android/
public class FirebaseChatMainApp extends Application {

    private static final String TAG = FirebaseChatMainApp.class.getSimpleName();
    private static DatabaseHelper databaseHelper;
    private static Context mContext;
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = DatabaseHelper.getDatabaseHelper(this);
        mContext = FirebaseChatMainApp.this;
    }

    public DatabaseHelper getDatabaseHelper() {
        if (null == databaseHelper) {
            databaseHelper = DatabaseHelper.getDatabaseHelper(this);
        }
        return databaseHelper;
    }

    public static Context getAppContext() {
        return mContext;
    }
}
