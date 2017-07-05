package edu.illinois.entm.sawbodeployer.LogFileDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogVideodbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "logvideo";
    private static final int DATABASE_VERSION = 1;

    public LogVideodbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LogVideoTable.TABLE_VIDEO_CREATE);
        db.execSQL(LogLanguageTable.TABLE_LANGUAGES_CREATE);
        db.execSQL(LogTopicTable.TABLE_TOPICS_CREATE);
        db.execSQL(LogCountryTable.TABLE_COUNTRY_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LogVideoTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LogTopicTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LogCountryTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LogLanguageTable.TABLE_NAME);
        onCreate(db);
    }
}
