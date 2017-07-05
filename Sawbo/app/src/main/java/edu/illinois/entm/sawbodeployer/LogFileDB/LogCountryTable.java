package edu.illinois.entm.sawbodeployer.LogFileDB;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogCountryTable {

    public static final String TABLE_NAME = "tbl_log_country";

    public static final String COLUMN_Country = "Country";


    public static final String[] allColumns = {
            COLUMN_Country
    };

    public static final String TABLE_COUNTRY_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_Country + " TEXT " +
            ")";
}
