package com.cuelogic.firebase.chat;

import android.app.Application;

//http://www.crazyhitty.com/one-to-one-chat-using-firebase-for-android/
public class FirebaseChatMainApp extends Application {

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
    }
}
