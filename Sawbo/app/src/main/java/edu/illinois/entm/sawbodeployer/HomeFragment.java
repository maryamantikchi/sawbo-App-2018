package edu.illinois.entm.sawbodeployer;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.illinois.entm.sawbodeployer.MyVideos.MyVideoFragment;
import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoLibraryFragment;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Mahsa on 4/1/2017.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    public HomeFragment() {
    }

    private Fragment fragment;
    private FragmentManager fragmentManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fragmentManager = getFragmentManager();

        Typeface btn_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");

        Button donate_btn = (Button) view.findViewById(R.id.btn_donate);
        donate_btn.setTypeface(btn_font);
        donate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse(getResources().getString(R.string.donate_url));
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        Button video_library_btn = (Button)view.findViewById(R.id.btn_video_library_home);
        video_library_btn.setTypeface(btn_font);
        video_library_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new VideoLibraryFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();

            }
        });

        Button my_video_btn = (Button)view.findViewById(R.id.btn_my_video_home);
        my_video_btn.setTypeface(btn_font);

        my_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment = new MyVideoFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();

            }
        });

        Button share_btn = (Button)view.findViewById(R.id.btn_share_home);
        share_btn.setTypeface(btn_font);

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                shareApplication();
            }
        });


        return view;
    }


    private void shareApplication() {
        ApplicationInfo app = getApplicationContext().getApplicationInfo();
        String filePath = app.sourceDir;

        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("*/*");

        File originalApk = new File(filePath);

        try {
            File tempFile = new File(getActivity().getExternalCacheDir() + "/ExtractedApk");
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
