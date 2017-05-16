package edu.illinois.entm.sawbodeployer.VideoDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mahsa on 4/28/2017.
 */

public class VideodbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myvideo";
    private static final int DATABASE_VERSION = 1;

    public VideodbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyVideoTable.TABLE_VIDEO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP  TABLE IF EXISTS " + MyVideoTable.TABLE_NAME);
        onCreate(db);
    }
}
