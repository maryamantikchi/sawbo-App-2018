package edu.illinois.entm.sawbodeployer.LogFileDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.VideoDB.MyVideoTable;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogVideoSource {

    private SQLiteDatabase database;
    private LogVideodbHelper dbHelper;

    public LogVideoSource(Context context) {
        dbHelper = new LogVideodbHelper(context);
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
        //values.put(LogVideoTable.COLUMN_POST_DATE, video.getPost_date());
        values.put(LogVideoTable.COLUMN_URL, video.getUrl());
        values.put(LogVideoTable.COLUMN_COUNTRY, video.getCountry());
        values.put(LogVideoTable.COLUMN_TOPIC, video.getTopic());
        values.put(LogVideoTable.COLUMN_LANGUAGE, video.getLanguage());
        values.put(LogVideoTable.COLUMN_f4v_file, (String) video.getF4v_file());
        values.put(LogVideoTable.COLUMN_IMAGE, video.getImage());
        values.put(LogVideoTable.COLUMN_VIDEO_LIGHT, video.getLite_file());
        values.put(LogVideoTable.COLUMN_VIDEO_mov_file, video.getMov_file());
        values.put(LogVideoTable.COLUMN_DESCRIPTION, video.getDescription());
       // values.put(LogVideoTable.COLUMN_ID, video.getID());
        values.put(LogVideoTable.COLUMN_id, video.getId());
        values.put(LogVideoTable.COLUMN_VIDEO_gp_file, video.getGp_file());
        values.put(LogVideoTable.COLUMN_TITLE, video.getTitle());
        values.put(LogVideoTable.COLUMN_mp4file, video.getMp4file());
        database.insert(LogVideoTable.TABLE_NAME, null, values);

        return video;
    }

    public String createTopic(String topic){
        ContentValues values = new ContentValues();
        values.put(LogTopicTable.COLUMN_Topic, topic);
        database.insert(LogTopicTable.TABLE_NAME, null, values);
        return topic;
    }

    public String createLanguage(String language){
        ContentValues values = new ContentValues();
        values.put(LogLanguageTable.COLUMN_Language, language);
        database.insert(LogLanguageTable.TABLE_NAME, null, values);
        return language;
    }

    public String createCountry(String country){
        ContentValues values = new ContentValues();
        values.put(LogCountryTable.COLUMN_Country, country);
        database.insert(LogCountryTable.TABLE_NAME, null, values);
        return country;
    }

    public void deleteAllVideo() {
      database.execSQL("DELETE FROM "+LogVideoTable.TABLE_NAME);

    }

    public void deleteAllCountry() {
        database.execSQL("DELETE FROM "+LogCountryTable.TABLE_NAME);

    }

    public void deleteAllLanguage() {
        database.execSQL("DELETE FROM "+LogLanguageTable.TABLE_NAME);

    }

    public void deleteAllTopics() {
        database.execSQL("DELETE FROM "+LogTopicTable.TABLE_NAME);

    }

    public List<all> getAllVideos() {
        List<all> videos = new ArrayList<all>();

        Cursor cursor = database.query(LogVideoTable.TABLE_NAME,
                LogVideoTable.allColumns, null, null, null, null, null);

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

    public List<String> getAllTopics() {
        List<String> topics = new ArrayList<String>();

        Cursor cursor = database.query(LogTopicTable.TABLE_NAME,
                LogTopicTable.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String topic = cursorToTopic(cursor);
            topics.add(topic);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return topics;
    }

    public List<String> getAllLanguage() {
        List<String> languages = new ArrayList<String>();

        Cursor cursor = database.query(LogLanguageTable.TABLE_NAME,
                LogLanguageTable.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String language = cursorToLanguage(cursor);
            languages.add(language);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return languages;
    }


    public List<String> getAllCountry() {
        List<String> countries = new ArrayList<String>();

        Cursor cursor = database.query(LogCountryTable.TABLE_NAME,
                LogCountryTable.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String country = cursorToCountry(cursor);
            countries.add(country);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return countries;
    }

    private all cursorToVideo(Cursor cursor) {
        all video = new all();
        //video.setPost_date(cursor.getLong(cursor.getColumnIndex(LogVideoTable.COLUMN_POST_DATE)));
        video.setUrl(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_URL)));
        video.setCountry(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_COUNTRY)));
        video.setTopic(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_TOPIC)));
        video.setLanguage(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_LANGUAGE)));
        video.setF4v_file(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_f4v_file)));
        video.setImage(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_IMAGE)));
        video.setVideolight(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_VIDEO_LIGHT)));
        video.setMov_file(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_VIDEO_mov_file)));
        video.setDescription(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_DESCRIPTION)));
        //video.setID(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_ID)));
        video.setId(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_id)));
        video.setGp_file(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_VIDEO_gp_file)));
        video.setTitle(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_TITLE)));
        video.setMp4file(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_mp4file)));
        return video;
    }

    private String cursorToTopic(Cursor cursor) {
        String topic = cursor.getString(cursor.getColumnIndex(LogTopicTable.COLUMN_Topic));
        return topic;
    }
    private String cursorToLanguage(Cursor cursor) {
        String language = cursor.getString(cursor.getColumnIndex(LogLanguageTable.COLUMN_Language));
        return language;
    }

    private String cursorToCountry(Cursor cursor) {
        String country = cursor.getString(cursor.getColumnIndex(LogCountryTable.COLUMN_Country));
        return country;
    }



    }
