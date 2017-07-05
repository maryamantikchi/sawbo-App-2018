package edu.illinois.entm.sawbodeployer.UserActivityDB;

/**
 * Created by Mahsa on 7/4/2017.
 */

public class UserActivityTable {

    public static final String TABLE_NAME = "tbl_user_activity";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APPID = "appid";
    public static final String COLUMN_USERID = "usrid";
    public static final String COLUMN_TIME_STAMP = "timestamp";
    public static final String COLUMN_IP = "ip";
    public static final String COLUMN_GPS_LAT = "coordinates_lat";
    public static final String COLUMN_GPS_LANG = "coordinates_lang";
    public static final String COLUMN_DL_VID_ID = "dl-vidID";
    public static final String COLUMN_blue_vidID = "blue-vidID";
    public static final String COLUMN_wifi_vidID = "wifi-vidID";
    public static final String COLUMN_fb_vidID = "fb-vidID";
    public static final String COLUMN_other_vidID = "other-vidID";






    public static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_APPID,
            COLUMN_USERID,
            COLUMN_TIME_STAMP,
            COLUMN_IP,
            COLUMN_GPS_LAT,
            COLUMN_GPS_LANG,
            COLUMN_DL_VID_ID,
            COLUMN_blue_vidID,
            COLUMN_wifi_vidID,
            COLUMN_fb_vidID,
            COLUMN_other_vidID
    };

    public static final String TABLE_USER_ACTIVITY_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
            COLUMN_APPID + " TEXT, " +
            COLUMN_USERID + " TEXT, " +
            COLUMN_TIME_STAMP + " TEXT, " +
            COLUMN_IP + " TEXT, " +
            COLUMN_GPS_LAT + " TEXT, " +
            COLUMN_GPS_LANG + " TEXT, " +
            COLUMN_DL_VID_ID + " TEXT, " +
            COLUMN_blue_vidID + " TEXT, " +
            COLUMN_wifi_vidID + " TEXT, " +
            COLUMN_fb_vidID + " TEXT, " +
            COLUMN_other_vidID + " TEXT " +
            ")";
}
