package edu.illinois.entm.sawbodeployer.VideoDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

/**
 * Created by Mahsa on 4/28/2017.
 */

public class MyVideoDataSource  {
    private SQLiteDatabase database;
    private VideodbHelper dbHelper;

    public MyVideoDataSource(Context context) {
        dbHelper = new VideodbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        System.err.println("open db");
    }

    public void close() {
        dbHelper.close();
        System.err.println("close db");

    }

    public all createVideo(all video) {
        ContentValues values = new ContentValues();
        values.put(MyVideoTable.COLUMN_ID, video.getId());
        values.put(MyVideoTable.COLUMN_COUNTRY, video.getCountry());
        values.put(MyVideoTable.COLUMN_LANGUAGE, video.getLanguage());
        values.put(MyVideoTable.COLUMN_TOPIC, video.getTopic());
        values.put(MyVideoTable.COLUMN_TITLE, video.getTitle());
        values.put(MyVideoTable.COLUMN_VIDEO, video.getVideo());
        values.put(MyVideoTable.COLUMN_VIDEO_LIGHT, video.getVideolight());
        values.put(MyVideoTable.COLUMN_DESCRIPTION, video.getDescription());
        values.put(MyVideoTable.COLUMN_IMAGE, video.getImage());
        database.insert(MyVideoTable.TABLE_NAME, null,
                values);

        return video;
    }

    public void deleteVideoLight(all video) {
        String videoFile = video.getVideolight();
        database.delete(MyVideoTable.TABLE_NAME, MyVideoTable.COLUMN_VIDEO_LIGHT
                + " = '" + videoFile +"'", null);
    }

    public void deleteVideoStandard(all video) {
        String videoFile = video.getVideo();
        database.delete(MyVideoTable.TABLE_NAME, MyVideoTable.COLUMN_VIDEO
                + " = '" + videoFile+"'", null);
    }

    public List<all> getAllVideos() {
        List<all> videos = new ArrayList<all>();

        Cursor cursor = database.query(MyVideoTable.TABLE_NAME,
                MyVideoTable.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            all video = cursorToVideo(cursor);
            videos.add(video);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return videos;
    }


    private all cursorToVideo(Cursor cursor) {
        all video = new all();
        video.setId(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_ID)));
        video.setCountry(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_COUNTRY)));
        video.setLanguage(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_LANGUAGE)));
        video.setTopic(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_TOPIC)));
        video.setTitle(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_TITLE)));
        video.setVideo(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_VIDEO)));
        video.setVideolight(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_VIDEO_LIGHT)));
        video.setDescription(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_DESCRIPTION)));
        video.setImage(cursor.getString(cursor.getColumnIndex(MyVideoTable.COLUMN_IMAGE)));
        return video;
    }


    public List<all> findDownloadVideos(String selected) {
        Cursor c = database.rawQuery("SELECT distinct * FROM tbl_my_video WHERE Video Like '" + selected + "' OR Videolight Like '" + selected+"' GROUP BY id", null);
        List<all> videos = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            all video = cursorToVideo(c);
            videos.add(video);
            c.moveToNext();
        }
        c.close();
        return videos;
    }

}
