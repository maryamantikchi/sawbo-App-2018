package edu.illinois.entm.sawbodeployer.LogFileDB;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogLanguageTable {
    public static final String TABLE_NAME = "tbl_log_language";

    public static final String COLUMN_Language = "Language";

    public static final String[] allColumns = {
            COLUMN_Language
    };

    public static final String TABLE_LANGUAGES_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_Language + " TEXT " +
            ")";
}
