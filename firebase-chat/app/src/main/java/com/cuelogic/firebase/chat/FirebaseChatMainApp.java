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
    private static String receiverUid = "";

    public static boolean isChattingWithSameUser(String uid) {
        return sIsChatActivityOpen && receiverUid.equals(uid);
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen, String receiverUid) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
        if(receiverUid != null)
            FirebaseChatMainApp.receiverUid = receiverUid;
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
