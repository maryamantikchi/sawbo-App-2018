package edu.illionois.entm.sawbodeployer.sawbo.VideoDetail;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rey.material.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import edu.illionois.entm.sawbodeployer.sawbo.R;
import edu.illionois.entm.sawbodeployer.sawbo.VideoDB.MyVideoDataSource;
import edu.illionois.entm.sawbodeployer.sawbo.VideoLibrary.all;

/**
 * Created by Mahsa on 4/9/2017.
 */

public class DownloadVideoFragment extends android.support.v4.app.Fragment{

    private MyVideoDataSource dataSource;
    Button liteFile,standardFile;
    public all video;
    View view;
    String fullTitle;

    boolean stopDownload = false;
    long downloadID;
    BroadcastReceiver onComplete;

    public DownloadVideoFragment(){
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    CheckPermissions();
                }
                break;
            default:
                break;
        }
    }

    public void CheckPermissions()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

         view = inflater.inflate(R.layout.fragment_download_video, container, false);

        initialize();

        return view;
    }
    void initialize(){

        if (Build.VERSION.SDK_INT >= 21) {
            CheckPermissions();
        }

        liteFile = (Button)view.findViewById(R.id.btn_download_lite);
        standardFile = (Button)view.findViewById(R.id.btn_download_standard);
        TextView title = (TextView)view.findViewById(R.id.title_video_download);
        Typeface title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");

        title.setTypeface(title_font);
        Typeface title_video_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        liteFile.setTypeface(title_video_font);
        standardFile.setTypeface(title_video_font);
        checkFileExist(video.getVideo(),standardFile);
        checkFileExist(video.getVideolight(),liteFile);

        liteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download_video(liteFile,video.getVideolight(),true);
            }
        });

        standardFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download_video(standardFile,video.getVideo(),false);
            }


        });

}
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private static boolean resolveEnable(Context context) {
        int state;
        try {
            state = context.getPackageManager()
                    .getApplicationEnabledSetting("com.android.providers.downloads");
        }catch(IllegalArgumentException e){
            return false;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }
    private void addDataBase(Boolean isLight){
        dataSource = new MyVideoDataSource(getContext());
        dataSource.open();
        // save the new comment to the database
        all newVideo = new all();
        newVideo = video;
        if (isLight)
            newVideo.setVideo("");
        else newVideo.setVideolight("");

        dataSource.createVideo(newVideo);
        dataSource.close();
    }


    private void download_video(final Button btn, final String urlType, final Boolean isLight){

        String[] title = urlType.split("_");
        fullTitle = title[1];


        if (stopDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getResources().getString(R.string.stopdownload_str))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            getActivity().unregisterReceiver(onComplete);
                            manager.remove(downloadID);
                            btn.setText("Download");
                            stopDownload = false;
                            btn.invalidate();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        } else {
            btn.setText("Stop download");
            stopDownload = true;
            btn.invalidate();

            if (resolveEnable(getActivity())) {
                try {
                    String urlvideo = URLEncoder.encode(urlType, "UTF-8").replace("+", "%20");
                    Log.v("encode url", urlvideo);

                    URL url = new URL(getResources().getString(R.string.video_url) + urlvideo);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.toString()));
                    request.setDescription("SAWBO video file");
                    request.setTitle(fullTitle);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    final File chkf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + urlType);
                    if (chkf.exists()) {
                        chkf.delete();
                    }
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, urlType);

                    DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                    downloadID = manager.enqueue(request);

                    onComplete = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {

                            addDataBase(isLight);

                            String path = context.getFilesDir() + "/";
                            File src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + urlType);
                            File dst = new File(path + urlType);
                            Log.d("filepath", src.toString());
                            try {
                                copy(src, dst);
                            } catch (IOException e) {

                            }
                            stopDownload = false;
                            btn.setText(context.getResources().getString(R.string.avoffline_str));
                            btn.setEnabled(false);
                            btn.invalidate();
                            if (chkf.exists()) {
                                chkf.delete();
                            }
                        }
                    };

                    getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                } catch (UnsupportedEncodingException e) {

                } catch (MalformedURLException e) {

                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Could not find Download Manager. If your device has a Download Manager, please ensure that it is enabled.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    //Open the specific App Info page:
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
                                    startActivity(intent);

                                } catch (ActivityNotFoundException e) {

                                    Toast.makeText(getActivity(), "Your phone is not compatible and will not be able to download videos.", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                stopDownload = false;
            }

        }
        }

    private void checkFileExist(String url,Button btn){
        File file = new File(getActivity().getFilesDir() + "/" + url );
        if(file.exists()) {
            btn.setText(getResources().getString(R.string.avoffline_str));
            btn.setEnabled(false);
        }
    }
}
