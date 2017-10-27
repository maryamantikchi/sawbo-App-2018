package edu.illinois.entm.sawbodeployer.VideoDetail;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.MyVideoDetail.MyVideoDetailFragment;
import edu.illinois.entm.sawbodeployer.MyVideos.MyVideoFragment;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.UserActivity.HelperActivity;
import edu.illinois.entm.sawbodeployer.UserActivity.IUserLogs;
import edu.illinois.entm.sawbodeployer.UserActivity.UserActivities;
import edu.illinois.entm.sawbodeployer.UserActivityDB.GPS;
import edu.illinois.entm.sawbodeployer.UserActivityDB.UserActivityDataSource;
import edu.illinois.entm.sawbodeployer.VideoDB.MyVideoDataSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Mahsa on 4/9/2017.
 */

public class DownloadVideoFragment extends android.support.v4.app.Fragment{

    private MyVideoDataSource dataSource;
    Button liteFile,standardFile;
    public all video;
    View view;


    boolean stopDownload = false;
    long downloadID;
    BroadcastReceiver onComplete;
    HelperActivity writeLog;
    ProgressDialog progress;
    IUserLogs api;

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

        if (Build.VERSION.SDK_INT >= 23) {
            CheckPermissions();
        }

        writeLog = new HelperActivity(getContext());

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
        checkFileExist(video.getGp_file(),standardFile,false);
        checkFileExist(video.getVideolight(),liteFile,true);

//        liteFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //addDataBase(true);
//
//
//                download_video(liteFile,video.getVideolight(),true);
//            }
//        });
//
//        standardFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // addDataBase(true);
//                download_video(standardFile,video.getGp_file(),false);
//            }
//
//
//        });



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
        all newVideo = new all();
        newVideo = video;
        if (isLight){
            newVideo.setGp_file("");
        }
        else newVideo.setVideolight("");

        dataSource.createVideo(newVideo);
        dataSource.close();
    }

    private void removeFromDB(boolean isLight){
            dataSource = new MyVideoDataSource(getContext());
            dataSource.open();
            if (isLight){
                dataSource.deleteVideoLight(video);
            }else {
                dataSource.deleteVideoStandard(video);
            }
            dataSource.close();

    }


    private void download_video(final Button btn, final String urlType, final Boolean isLight){

        if (stopDownload) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getResources().getString(R.string.stopdownload_str))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            removeFromDB(isLight);
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            getActivity().unregisterReceiver(onComplete);
                            manager.remove(downloadID);
                           // manager.remove(downloadIDImg);
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
                    final URL url = new URL(getResources().getString(R.string.video_url) + urlvideo);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url.toString()));
                    request.setDescription("SAWBO video file");
                    request.setTitle(video.getTitle());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    final File chkf = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + urlType);
                    if (chkf.exists()) {
                        chkf.delete();
                    }
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, urlType);

                    final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

                    downloadID = manager.enqueue(request);

                  /*  try {
                        String urlImage = URLEncoder.encode(video.getImage(), "UTF-8").replace("+", "%20");
                        final URL url_image = new URL(getResources().getString(R.string.thumbnail_url)+urlImage);
                        //saveToInternalStorage(downloadImage(url_image),video.getImage());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }*/

                    onComplete = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {

                            String path = context.getFilesDir() + "/";
                            File src = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + urlType);
                            File dst = new File(path + urlType);

                                copy(src, dst);

                            } catch (IOException e) {

                            }
                            stopDownload = false;
                            btn.setText(context.getResources().getString(R.string.avoffline_str));
                            btn.setEnabled(true);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MyVideoDetailFragment fragment = new MyVideoDetailFragment();
                                    fragment.videoDetail = video;

                                    MyVideoFragment myVideoFragment = new MyVideoFragment();

                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction().hide(myVideoFragment)
                                            .add(R.id.main_container, fragment)
                                            .addToBackStack(null)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                            .commit();
                                }
                            });
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


            /*String videoFilename = "";
            if (isLight) videoFilename = video.getVideolight();
            else videoFilename = video.getGp_file();*/
//            wl.writeNow(getActivity(), "download", videoFilename, "");
  //          wl.sendLog(getActivity());
//            UserActivities activities = new UserActivities();
//            activities.setDl_vidID(video.getId());
//            writeLog.WriteUsrActivity(activities,getActivity());

            addDataBase(isLight);




        }


        }

    private void checkFileExist(final String url, final Button btn, final boolean isLight){
        File file = new File(getActivity().getFilesDir() + "/" + url );
        if(file.exists()) {
            btn.setText(getResources().getString(R.string.avoffline_str));
            btn.setEnabled(true);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyVideoDetailFragment fragment = new MyVideoDetailFragment();
                    fragment.videoDetail = video;
                    if (isLight)
                    fragment.videoDetail.setGp_file("");
                    else fragment.videoDetail.setLite_file("");

                    MyVideoDetailFragment myVideoFragment = new MyVideoDetailFragment();

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().hide(myVideoFragment)
                            .add(R.id.main_container, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });
        }else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //addDataBase(true);
                    if (isOnline(getContext())) {

                        progress = ProgressDialog.show(getContext(), "Loading",
                            "Fetching data...", true);

                    new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            HelperActivity writeLog = new HelperActivity(getContext());
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            String ip = preferences.getString("IP", "");
                            if (ip==null||ip==""){
                                ip = writeLog.getIP(getActivity());
                            }
                              //  UserActivityDataSource dataSource = new UserActivityDataSource(getContext());
                               // dataSource.open();
                            UserActivities activities = new UserActivities();
                            activities.setIp(ip);
                            GPS gps = writeLog.getGPSs(getActivity());
                            activities.setGPS(gps);
                            activities.setCity(writeLog.getCityName(getActivity(),gps));
                            activities.setCountry(writeLog.getCountryName(getActivity(),gps));
                            activities.setAppid("googleplay");
                            activities.setDl_vidID(video.getId());
                            activities.setTimestamp(writeLog.getTimeStamp());
                            String UID = preferences.getString("UsrID", "");
                            if (UID==null || UID=="")
                                UID = writeLog.getdID(getActivity());
                            activities.setUsrid(UID);


                                List<UserActivities> user_logs = new ArrayList<UserActivities>();
                                user_logs.add(activities);

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("https://2svz9cfvr4.execute-api.us-west-2.amazonaws.com/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                api = retrofit.create(IUserLogs.class);

                                postRequest(user_logs);


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    progress.dismiss();
                                }
                            });
                        }
                    }).start();
                        download_video(btn,url,isLight);
                    }else {
                    Toast.makeText(getContext(),getResources().getString(R.string.nointernet),Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }


//    public static Bitmap downloadImage(URL encodedUrl) {
//        try {
//            HttpURLConnection connection = (HttpURLConnection) encodedUrl.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream is = connection.getInputStream();
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = false;
//            options.inSampleSize = 1;
//            Bitmap bitmap = BitmapFactory.decodeStream(is,null, options);
//            is.close();
//            return bitmap;
//        }
//        catch (Exception e)
//        {
//            return null;
//        }
//    }

/*    public static void saveToInternalStorage(Bitmap bitmapImage, String icon){
        File root = Environment.getExternalStorageDirectory();
        File Dir=null;
            Dir = new File(root.getAbsolutePath() +"/.Sawbo/Images");
       // if (isLight) icon += "_light";
        File file = new File(Dir, icon);


        if (!file.exists()) {
            if (bitmapImage != null) {
                file.getParentFile().mkdirs();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private void postRequest(List<UserActivities> product) {
        Call<UserActivities> call = api.CreateProduct(product);

        call.enqueue(new Callback<UserActivities>() {
            @Override
            public void onResponse(Response<UserActivities> response, Retrofit retrofit) {
            }

            @Override
            public void onFailure(Throwable t) {
                writeLog.WriteUsrActivity(product.get(0),getActivity());

            }
        });

    }

}
