package edu.entm.illinois.edu.sawbo.VideoDB;

/**
 * Created by Mahsa on 4/28/2017.
 */

public class MyVideoTable {

    public static final String TABLE_NAME = "tbl_my_video";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_COUNTRY = "Country";
    public static final String COLUMN_LANGUAGE = "Language";
    public static final String COLUMN_TOPIC = "Topic";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_VIDEO = "Video";
    public static final String COLUMN_VIDEO_LIGHT = "Videolight";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_IMAGE = "Image";


    public static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_COUNTRY,
            COLUMN_LANGUAGE,
            COLUMN_TOPIC,
            COLUMN_TITLE,
            COLUMN_VIDEO,
            COLUMN_VIDEO_LIGHT,
            COLUMN_DESCRIPTION,
            COLUMN_IMAGE
    };
    public static final String TABLE_VIDEO_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " TEXT " +
            COLUMN_COUNTRY + " TEXT ," +
            COLUMN_LANGUAGE + " TEXT ," +
            COLUMN_TOPIC + " TEXT ," +
            COLUMN_TITLE + " TEXT ," +
            COLUMN_VIDEO + " TEXT ," +
            COLUMN_VIDEO_LIGHT + " TEXT ," +
            COLUMN_DESCRIPTION + " TEXT ," +
            COLUMN_IMAGE + " TEXT " +
            ")";
}
