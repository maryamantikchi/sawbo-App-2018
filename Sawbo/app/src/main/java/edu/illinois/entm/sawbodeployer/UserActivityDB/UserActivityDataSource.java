package edu.illinois.entm.sawbodeployer.UserActivityDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.UserActivity.UserActivities;

/**
 * Created by Mahsa on 7/4/2017.
 */

public class UserActivityDataSource {
    private SQLiteDatabase database;
    private UserActivitydbHelper dbHelper;

    public UserActivityDataSource(Context context) {
        dbHelper = new UserActivitydbHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        System.err.println("open db");
    }

    public void close() {
        dbHelper.close();
        System.err.println("close db");

    }

    public UserActivities createUserActivity(UserActivities usrActv) {
        ContentValues usrActvs = new ContentValues();
        usrActvs.put(UserActivityTable.COLUMN_APPID, usrActv.getAppid());
        usrActvs.put(UserActivityTable.COLUMN_USERID, usrActv.getUsrid());
        usrActvs.put(UserActivityTable.COLUMN_TIME_STAMP, usrActv.getTimestamp());
        usrActvs.put(UserActivityTable.COLUMN_IP, usrActv.getIp());
        usrActvs.put(UserActivityTable.COLUMN_GPS_LAT, usrActv.getGPS()[0]);
        usrActvs.put(UserActivityTable.COLUMN_GPS_LANG, usrActv.getGPS()[1]);
        usrActvs.put(UserActivityTable.COLUMN_DL_VID_ID, usrActv.getDl_vidID());
        usrActvs.put(UserActivityTable.COLUMN_blue_vidID, usrActv.getBlue_vidID());
        usrActvs.put(UserActivityTable.COLUMN_wifi_vidID, usrActv.getWifi_vidID());
        usrActvs.put(UserActivityTable.COLUMN_fb_vidID, usrActv.getFb_vidID());
        usrActvs.put(UserActivityTable.COLUMN_other_vidID, usrActv.getOther_vidID());
        database.insert(UserActivityTable.TABLE_NAME, null, usrActvs);

        return usrActv;
    }

    public void deleteAllActivities() {
        database.execSQL("DELETE FROM "+UserActivityTable.TABLE_NAME);

    }


    public List<UserActivities> getUserActivities(String ip) {
        List<UserActivities> activities = new ArrayList<UserActivities>();

        Cursor cursor = database.query(UserActivityTable.TABLE_NAME,
                UserActivityTable.allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserActivities usrAct = cursorToUsrAct(cursor,ip);
            activities.add(usrAct);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return activities;
    }


    private UserActivities cursorToUsrAct(Cursor cursor,String ip) {
        UserActivities usrAct = new UserActivities();
        String[] gps = {cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_GPS_LAT)),
                cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_GPS_LANG))};
        usrAct.setId(cursor.getInt(cursor.getColumnIndex(UserActivityTable.COLUMN_ID)));
        usrAct.setAppid(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_APPID)));
        usrAct.setUsrid(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_USERID)));
        usrAct.setTimestamp(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_TIME_STAMP)));
        usrAct.setIp(ip);
        usrAct.setGPS(gps);
        usrAct.setDl_vidID(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_DL_VID_ID)));
        usrAct.setBlue_vidID(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_blue_vidID)));
        usrAct.setWifi_vidID(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_wifi_vidID)));
        usrAct.setFb_vidID(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_fb_vidID)));
        usrAct.setOther_vidID(cursor.getString(cursor.getColumnIndex(UserActivityTable.COLUMN_other_vidID)));

        return usrAct;
    }
}
