package edu.illinois.entm.sawbodeployer;


import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Toast;

import com.rey.material.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.AboutContact.InfoFragment;
import edu.illinois.entm.sawbodeployer.MyVideos.MyVideoFragment;
import edu.illinois.entm.sawbodeployer.UserActivity.HelperActivity;
import edu.illinois.entm.sawbodeployer.UserActivity.IUserLogs;
import edu.illinois.entm.sawbodeployer.UserActivity.UserActivities;
import edu.illinois.entm.sawbodeployer.UserActivityDB.UserActivityDataSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.Video;
import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoLibraryFragment;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ImageButton home,video_library,my_videos,share,info;
    private AppCompatImageButton setting,search;
    IUserLogs api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

    }
    public void initialize(){

        if (isOnline(this)){
            HelperActivity writeLog = new HelperActivity(this);
            String ip = writeLog.getIP();
            UserActivityDataSource dataSource = new UserActivityDataSource(this);
            dataSource.open();
            List<UserActivities> user_logs = new ArrayList<UserActivities>();
            user_logs = dataSource.getUserActivities(ip);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://2svz9cfvr4.execute-api.us-west-2.amazonaws.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(IUserLogs.class);

            postRequest(user_logs,dataSource);

        }
        home = (ImageButton)findViewById(R.id.btn_home);
        home.setOnClickListener(this);
        video_library = (ImageButton)findViewById(R.id.btn_video_library);
        video_library.setOnClickListener(this);
        my_videos = (ImageButton)findViewById(R.id.btn_my_video);
        my_videos.setOnClickListener(this);
        share = (ImageButton)findViewById(R.id.btn_share);
        share.setOnClickListener(this);
        info = (ImageButton)findViewById(R.id.btn_info);
        info.setOnClickListener(this);

        setting = (AppCompatImageButton)findViewById(R.id.setting_btn);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment newFragment = new SettingFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        search = (AppCompatImageButton)findViewById(R.id.search_compat);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment newFragment = new SearchFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        fragmentManager = getSupportFragmentManager();
        fragment = new HomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_home:
                fragment = new HomeFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                break;
            case R.id.btn_video_library:
                fragment = new VideoLibraryFragment();
                FragmentTransaction transaction_videolibrary = fragmentManager.beginTransaction();
                transaction_videolibrary.replace(R.id.main_container, fragment).commit();
                break;
            case R.id.btn_my_video:
                fragment = new MyVideoFragment();
                FragmentTransaction transaction_myvideo = fragmentManager.beginTransaction();
                transaction_myvideo.replace(R.id.main_container, fragment).commit();
                break;
            case R.id.btn_share:
                shareApplication();
                break;
            case R.id.btn_info:
                fragment = new InfoFragment();
                fragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit();
                break;
        }

    }


    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("*/*");

        File originalApk = new File(filePath);

        try {
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk");
            if (!tempFile.isDirectory())
                if (!tempFile.mkdirs())
                    return;
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
            if (!tempFile.exists()) {
                if (!tempFile.createNewFile()) {
                    return;
                }
            }
            InputStream in = new FileInputStream(originalApk);
            OutputStream out = new FileOutputStream(tempFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
            startActivity(Intent.createChooser(intent, "Share app via"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void postRequest(List<UserActivities> product, final UserActivityDataSource dataSource) {

         Call<UserActivities> call = api.CreateProduct(product);


        call.enqueue(new Callback<UserActivities>() {

            @Override
            public void onResponse(Response<UserActivities> response, Retrofit retrofit) {
                dataSource.deleteAllActivities();
                dataSource.close();
            }

            @Override
            public void onFailure(Throwable t) {
                dataSource.close();
            }
        });

    }
/*
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this,getResources().getString(R.string.twice), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce=false;

                if (getIntent().getBooleanExtra("EXIT", false)) {
                    finish();
                }


            }
        }, 2000);
    }*/
}
