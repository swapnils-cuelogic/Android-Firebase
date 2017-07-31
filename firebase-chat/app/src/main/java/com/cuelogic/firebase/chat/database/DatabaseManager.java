package com.cuelogic.firebase.chat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Harshal Vibhandik on 12/05/17.
 */

public class DatabaseManager {
    protected Context mContext;

    protected SQLiteDatabase getWritableDatabase() {
        return DatabaseOpenHelper.getInstance(mContext).getWritableDatabase();
    }
    protected SQLiteDatabase getReadableDatabase() {
        return DatabaseOpenHelper.getInstance(mContext).getReadableDatabase();
    }
}
