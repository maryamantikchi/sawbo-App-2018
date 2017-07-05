package edu.illinois.entm.sawbodeployer;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;

import com.rey.material.widget.ImageButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.illinois.entm.sawbodeployer.AboutContact.InfoFragment;
import edu.illinois.entm.sawbodeployer.MyVideos.MyVideoFragment;
import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoLibraryFragment;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ImageButton home,video_library,my_videos,share,info;
    private AppCompatImageButton setting,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

    }




    public void initialize(){
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
                FragmentTransaction transaction_info = fragmentManager.beginTransaction();
                transaction_info.replace(R.id.main_container, fragment).commit();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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

}
