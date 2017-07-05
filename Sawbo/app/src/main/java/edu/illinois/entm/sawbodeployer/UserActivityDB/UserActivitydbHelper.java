package edu.illinois.entm.sawbodeployer.UserActivityDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.illinois.entm.sawbodeployer.LogFileDB.LogVideoTable;

/**
 * Created by Mahsa on 7/4/2017.
 */

public class UserActivitydbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "user_info";
    private static final int DATABASE_VERSION = 1;

    public UserActivitydbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UserActivityTable.TABLE_USER_ACTIVITY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserActivityTable.TABLE_NAME);

    }
}
