package com.cuelogic.firebase.chat;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

//http://www.crazyhitty.com/one-to-one-chat-using-firebase-for-android/
public class FirebaseChatMainApp extends Application {

    private static final String TAG = FirebaseChatMainApp.class.getSimpleName();
    //private static DatabaseHelper databaseHelper;
    private static Context mContext;
    private static boolean isChatActivityOpen = false;
    private static boolean isRoomsActivityOpen = false;
    private static String receiverUid = "";

    public static boolean isRoomsOpen() {
        return isRoomsActivityOpen;
    }

    public static boolean isChattingWithSameUser(String uid) {
        return isChatActivityOpen && receiverUid.equals(uid);
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen, String receiverUid) {
        FirebaseChatMainApp.isChatActivityOpen = isChatActivityOpen;
        if(receiverUid != null)
            FirebaseChatMainApp.receiverUid = receiverUid;
    }

    public static void setRoomsActivityOpen(boolean isRoomsActivityOpen) {
        FirebaseChatMainApp.isRoomsActivityOpen = isRoomsActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //databaseHelper = DatabaseHelper.getDatabaseHelper(this);
        mContext = FirebaseChatMainApp.this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    /*public DatabaseHelper getDatabaseHelper() {
        if (null == databaseHelper) {
            databaseHelper = DatabaseHelper.getDatabaseHelper(this);
        }
        return databaseHelper;
    }*/

    public static Context getAppContext() {
        return mContext;
    }
}
