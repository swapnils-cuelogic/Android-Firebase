package com.cuelogic.firebase.chat.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cuelogic.firebase.chat.utils.Logger;

/**
 * Reference: http://stackoverflow.com/questions/13373170/greendao-schema-update-and-data-migration/30334668#30334668
 * https://github.com/yuweiguocn/GreenDaoUpgradeHelper
 *
 * @version 1.0.0
 * @Date 14/04/2017
 */

public class MigrateSQLiteOpenHelper extends DaoMaster.DevOpenHelper {

    private static final String TAG = MigrateSQLiteOpenHelper.class.getSimpleName();

    public MigrateSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.vLog(TAG, "Upgrading schema from version " + oldVersion + " to " + newVersion + " by migrating all tables data");
        try {
            MigrationHelper.migrate(db, SampleDao.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
