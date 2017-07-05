package edu.illinois.entm.sawbodeployer.LogFileDB;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogVideoTable {
    public static final String TABLE_NAME = "tbl_log_video";

   // public static final String COLUMN_POST_DATE = "post_date";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_COUNTRY = "Country";
    public static final String COLUMN_TOPIC = "Topic";
    public static final String COLUMN_LANGUAGE = "Language";
    public static final String COLUMN_f4v_file = "f4v_file";
    public static final String COLUMN_IMAGE = "Image";
    public static final String COLUMN_VIDEO_LIGHT = "Videolight";
    public static final String COLUMN_VIDEO_mov_file = "mov_file";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_id = "id";
   // public static final String COLUMN_ID = "ID";
    public static final String COLUMN_VIDEO_gp_file = "gp_file";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_mp4file = "mp4file";

    public static final String[] allColumns = {
            //COLUMN_POST_DATE,
            COLUMN_URL,
            COLUMN_COUNTRY,
            COLUMN_TOPIC,
            COLUMN_LANGUAGE,
            COLUMN_f4v_file,
            COLUMN_IMAGE,
            COLUMN_VIDEO_LIGHT,
            COLUMN_VIDEO_mov_file,
            COLUMN_DESCRIPTION,
            COLUMN_id,
            //COLUMN_ID,
            COLUMN_VIDEO_gp_file,
            COLUMN_TITLE,
            COLUMN_mp4file
    };

    public static final String TABLE_VIDEO_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
          //  COLUMN_POST_DATE + " INTEGER , " +
            COLUMN_URL + " TEXT , " +
            COLUMN_COUNTRY + " TEXT ," +
            COLUMN_TOPIC + " TEXT ," +
            COLUMN_LANGUAGE + " TEXT ," +
            COLUMN_f4v_file + " TEXT ," +
            COLUMN_IMAGE + " TEXT ," +
            COLUMN_VIDEO_LIGHT + " TEXT ," +
            COLUMN_VIDEO_mov_file + " TEXT ," +
            COLUMN_DESCRIPTION + " TEXT ," +
            COLUMN_id + " TEXT , " +
           // COLUMN_ID + " TEXT , " +
            COLUMN_VIDEO_gp_file + " TEXT , " +
            COLUMN_TITLE + " TEXT ," +
            COLUMN_mp4file + " TEXT " +
            ")";
}
