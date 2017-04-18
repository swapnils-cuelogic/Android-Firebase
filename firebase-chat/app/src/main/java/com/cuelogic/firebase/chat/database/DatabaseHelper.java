package com.cuelogic.firebase.chat.database;

import android.content.Context;

import com.cuelogic.firebase.chat.utils.Logger;

import org.greenrobot.greendao.database.Database;

/**
 * @version 1.0.0
 * @Date 14/04/2017
 */

public class DatabaseHelper {


    private static final String TAG = DatabaseHelper.class.getSimpleName();

    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    private static final boolean ENCRYPTED = true;

    private static DatabaseHelper databaseHelper;
    private DaoSession daoSession;
    private SampleDao mSampleDao;

    private DatabaseHelper() {
    }

    private DatabaseHelper(Context context) {
        // GreenDAO initialization
        MigrateSQLiteOpenHelper helper = new MigrateSQLiteOpenHelper(context,
                ENCRYPTED ? "notes-db-encrypted" : "notes-db", null);
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret")
                : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DatabaseHelper getDatabaseHelper(Context context) {
        Logger.vLog(TAG, "getDatabaseHelper");
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    public SampleDao getSampleDao() {
        mSampleDao = daoSession.getSampleDao();
        return mSampleDao;
    }
}
