package edu.illinois.entm.sawbodeployer.LogFileDB;

/**
 * Created by Mahsa on 6/29/2017.
 */

public class LogTopicTable {
    public static final String TABLE_NAME = "tbl_log_topic";

    public static final String COLUMN_Topic = "Topic";

    public static final String[] allColumns = {
            COLUMN_Topic
    };

    public static final String TABLE_TOPICS_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_Topic + " TEXT " +
            ")";
}
