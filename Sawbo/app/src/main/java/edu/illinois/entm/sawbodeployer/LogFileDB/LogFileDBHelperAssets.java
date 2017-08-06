package edu.illinois.entm.sawbodeployer.LogFileDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by Mahsa on 8/6/2017.
 */

public class LogFileDBHelperAssets extends SQLiteOpenHelper {

    private final Context myContext;
    private static final String DATABASE_NAME = "swabo_video.sqlite";
    private static final int DATABASE_VERSION = 1;
    private String pathToSaveDBFile;
    public LogFileDBHelperAssets(Context context, String filePath) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        filePath = context.getFilesDir() + "/";
        pathToSaveDBFile = new StringBuffer(filePath).append(DATABASE_NAME).toString();
    }

    public void prepareDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        if(!dbExist) {
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        OutputStream os = new FileOutputStream(pathToSaveDBFile);
        InputStream is = myContext.getAssets().open("db/"+DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<all> getAllVideos() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT id,post_date,idn,country,description,f4v_file,gp_file,image,language,lite_file,mov_file,mp4file,title,topic,url FROM sawbo_video";
        Cursor cursor = db.rawQuery(query, null);
        List<all> list = new ArrayList<all>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            all video = cursorToVideo(cursor);
            list.add(video);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        db.close();
        return list;
    }

    //select DISTINCT country from sawbo_video
    public List<String> getCountries() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "select DISTINCT country from sawbo_video";
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String country = cursor.getString(0);
            list.add(country);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getTopics() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "select DISTINCT topic from sawbo_video";
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String topic = cursor.getString(0);
            list.add(topic);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getLanguages() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "select DISTINCT language from sawbo_video";
        Cursor cursor = db.rawQuery(query, null);

        List<String> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String language = cursor.getString(0);
            list.add(language);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    private all cursorToVideo(Cursor cursor) {
        all video = new all();
        //video.setPost_date(cursor.getLong(cursor.getColumnIndex(LogVideoTable.COLUMN_POST_DATE)));
        video.setUrl(cursor.getString(cursor.getColumnIndex("url")));
        video.setCountry(cursor.getString(cursor.getColumnIndex("country")));
        video.setTopic(cursor.getString(cursor.getColumnIndex("topic")));
        video.setLanguage(cursor.getString(cursor.getColumnIndex("language")));
        video.setF4v_file(cursor.getString(cursor.getColumnIndex("f4v_file")));
        video.setImage(cursor.getString(cursor.getColumnIndex("image")));
        video.setVideolight(cursor.getString(cursor.getColumnIndex("lite_file")));
        video.setMov_file(cursor.getString(cursor.getColumnIndex("mov_file")));
        video.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        //video.setID(cursor.getString(cursor.getColumnIndex(LogVideoTable.COLUMN_ID)));
        video.setId(cursor.getString(cursor.getColumnIndex("id")));
        video.setGp_file(cursor.getString(cursor.getColumnIndex("gp_file")));
        video.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        video.setMp4file(cursor.getString(cursor.getColumnIndex("mp4file")));
        return video;
    }
}
