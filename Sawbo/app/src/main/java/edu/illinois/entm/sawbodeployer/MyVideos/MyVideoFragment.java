package edu.illinois.entm.sawbodeployer.MyVideos;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.illinois.entm.sawbodeployer.LogFileDB.LogVideoSource;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.VideoDB.MyVideoDataSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.DividerItemDecoration;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Mahsa on 4/1/2017.
 */
public class MyVideoFragment extends android.support.v4.app.Fragment {
    View view;
    TextView title1,title2;
    Typeface title_font, title_list_font;
    MyVideoDataSource dataSource;
    LogVideoSource logDateSource;
    RecyclerView videoList;
    edu.illinois.entm.sawbodeployer.MyVideos.MyVideoListAdapter adapter;
    AsyncTask<Void, Void, Void> getData;
    ProgressBar progressBar;

    TextView no_video;

    CheckBox select_all;

    LinearLayout delete_video;

    public MyVideoFragment(){
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getData = new MyVideoFragment.getData().execute();

                } else {

                    CheckPermissions();
                }
                break;
            default:
                break;
        }
    }



    ArrayList<all> videos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Fabric.with(getActivity(), new Crashlytics());
        view = inflater.inflate(R.layout.fragment_my_video, container, false);
            initialize();
        return view;

    }

    private void initialize(){

        if (Build.VERSION.SDK_INT >= 23){
            CheckPermissions();
        }else {
            getData = new MyVideoFragment.getData().execute();
        }
        title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");

        title_list_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");

        title1 = (TextView)view.findViewById(R.id.title_my_video_library);
        title2 = (TextView)view.findViewById(R.id.title_my_video);
        title1.setTypeface(title_font);
        title2.setTypeface(title_font);



        videoList = (RecyclerView)view.findViewById(R.id.recycler_view_my_vide);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        videoList.setLayoutManager(mLayoutManager);
        videoList.setItemAnimator(new DefaultItemAnimator());
        videoList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar_my_video);

        select_all = (CheckBox)view.findViewById(R.id.select_all_checkbox);
        select_all.setTypeface(title_list_font);

        delete_video = (LinearLayout)view.findViewById(R.id.delete_video_selected);

        no_video = (TextView)view.findViewById(R.id.my_no_video);
        no_video.setTypeface(title_list_font);


        select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                adapter.selectAll(isChecked);

            }
        });

        delete_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete these videos?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                List<all> removeVideoList = adapter.getSelectedVideo();
                                for (all video:removeVideoList) {
                                    deleteVideo(video);
                                }
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(MyVideoFragment.this).attach(MyVideoFragment.this).commit();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


    }

    private class getData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (videos.size()!=0){
                videoList.setAdapter(adapter);
                videoList.setVisibility(View.VISIBLE);
                no_video.setVisibility(View.GONE);
            }else{
                videoList.setVisibility(View.GONE);
                no_video.setVisibility(View.VISIBLE);

            }


            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {



                adapter = new MyVideoListAdapter(gettVideos(),getContext());
            return null;
        }
    }



    public void CheckPermissions()
    {

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }else {
            getData = new MyVideoFragment.getData().execute();
        }
    }

    private List<all> gettVideos(){
        dataSource = new MyVideoDataSource(getContext());
        dataSource.open();
        videos = new ArrayList<all>();

        File dirFiles = getContext().getFilesDir();
        final ArrayList<String> videoFile = new ArrayList<String>();

        for (String strFile : dirFiles.list()){
            String extension = strFile.substring(strFile.lastIndexOf(".")+1);
            if(extension.equals("3gp") || extension.equals("mp4")){
                videoFile.add(strFile);
                for (int i=0;i<dataSource.findDownloadVideos(strFile).size();i++){
                    videos.add(dataSource.findDownloadVideos(strFile).get(i));
                }


            }
            Log.v("filename", strFile);
        }

//        String bluetoothFile = searchForBluetoothFolder();
//        File bluetoothFolder = new File(bluetoothFile);

        File sdCard = Environment.getExternalStorageDirectory();
        String bluetoothFile = sdCard.getAbsolutePath() + "/Bluetooth";
        File bluetoothFolder = new File(bluetoothFile);
        if (!bluetoothFolder.exists()){
             bluetoothFile = sdCard.getAbsolutePath() + "/bluetooth";
             bluetoothFolder = new File(bluetoothFile);
        }

        if (bluetoothFolder.exists()){
            logDateSource = new LogVideoSource(getContext());

            logDateSource.open();
            List<all> videosdb = new ArrayList<>();
            videosdb = logDateSource.getAllVideos();
            logDateSource.close();


                for (String strFile : bluetoothFolder.list()){
                    String extension = strFile.substring(strFile.lastIndexOf(".")+1);
                    if(extension.equals("3gp") || extension.equals("mp4")){
                      //  videoFile.add(strFile);
                        for (int i=0;i<videosdb.size();i++){
                            if ((videosdb.get(i).getGp_file()!=null && videosdb.get(i).getGp_file().equalsIgnoreCase(strFile)) ||
                                    (videosdb.get(i).getVideolight()!= null && videosdb.get(i).getVideolight().equalsIgnoreCase(strFile) )||
                                    (videosdb.get(i).getLite_file()!= null && videosdb.get(i).getLite_file().equalsIgnoreCase(strFile)) ||
                                    (videosdb.get(i).getMov_file()!=null && videosdb.get(i).getMov_file().equalsIgnoreCase(strFile))||
                                    (videosdb.get(i).getMp4file() !=null && videosdb.get(i).getMp4file().equalsIgnoreCase(strFile))) {
                                // dataSource.createVideo(videosdb.get(i));
                                File f= new File(bluetoothFile,strFile);
                                videosdb.get(i).setGp_file(String.valueOf(Uri.fromFile(f)));
                                videosdb.get(i).setLite_file(String.valueOf(Uri.fromFile(f)));
                                videos.add(videosdb.get(i));

                            }
                        }

                    }
                    Log.v("filename", strFile);
                }
            }


        dataSource.close();

        return videos;
    }



    private void deleteVideo(all video){
        dataSource = new MyVideoDataSource(getContext());
        dataSource.open();

        boolean isLight = false;

        String filename = "";
        if (video.getGp_file() == null || video.getGp_file().equals("")){
            isLight = true;
            filename = video.getVideolight();
        }else if(video.getVideolight() == null || video.getVideolight().equals("")){
            isLight = false;
            filename = video.getGp_file();
        }

        File file = new File(getActivity().getFilesDir() + "/" + filename);

            file.delete();

            File root = Environment.getExternalStorageDirectory();
            File Dir=null;
            Dir = new File(root.getAbsolutePath() +"/.Sawbo/Images");
            String icon = video.getImage();
            if (isLight) icon += "_light";
            File fileImg = new File(Dir, icon);


            if (fileImg.exists()) {
                fileImg.delete();
            }

            if (isLight){
                dataSource.deleteVideoLight(video);
            }else {
                dataSource.deleteVideoStandard(video);
            }

        File sdCard = Environment.getExternalStorageDirectory();
        String bluetoothFile = sdCard.getAbsolutePath() + "/Bluetooth";
        File bluetoothFolder = new File(bluetoothFile);

        if (!bluetoothFolder.exists()){
            bluetoothFile = sdCard.getAbsolutePath() + "/bluetooth";
            bluetoothFolder = new File(bluetoothFile);
        }


//        String bluetoothFile = searchForBluetoothFolder();
//        File bluetoothFolder = new File(bluetoothFile);



        if (bluetoothFolder.exists()){
            logDateSource = new LogVideoSource(getContext());

            logDateSource.open();
            List<all> videosdb = new ArrayList<>();
            videosdb = logDateSource.getAllVideos();
            logDateSource.close();


            for (String strFile : bluetoothFolder.list()){
                String extension = strFile.substring(strFile.lastIndexOf(".")+1);
                if(extension.equals("3gp") || extension.equals("mp4")){
                    //  videoFile.add(strFile);
                    for (int i=0;i<videosdb.size();i++){
                        if ((videosdb.get(i).getGp_file()!=null && videosdb.get(i).getGp_file().equalsIgnoreCase(strFile)) ||
                                (videosdb.get(i).getVideolight()!= null && videosdb.get(i).getVideolight().equalsIgnoreCase(strFile) )||
                                (videosdb.get(i).getLite_file()!= null && videosdb.get(i).getLite_file().equalsIgnoreCase(strFile)) ||
                                (videosdb.get(i).getMov_file()!=null && videosdb.get(i).getMov_file().equalsIgnoreCase(strFile))||
                                (videosdb.get(i).getMp4file() !=null && videosdb.get(i).getMp4file().equalsIgnoreCase(strFile))) {
                            // dataSource.createVideo(videosdb.get(i));
                            File f= new File(bluetoothFile,strFile);
                            f.delete();

                        }
                    }

                }
                Log.v("filename", strFile);
            }
        }

        dataSource.close();


    }

    public List<File> folderSearchBT(File src, String folder)
            throws FileNotFoundException {

        List<File> result = new ArrayList<File>();

        File[] filesAndDirs = src.listFiles();
        List<File> filesDirs = Arrays.asList(filesAndDirs);

        for (File file : filesDirs) {
            result.add(file); // always add, even if directory
            if (!file.isFile()) {
                List<File> deeperList = folderSearchBT(file, folder);
                result.addAll(deeperList);
            }
        }
        return result;
    }

    public String searchForBluetoothFolder() {

        String splitchar = "/";
        File root = Environment.getExternalStorageDirectory();
        List<File> btFolder = null;
        String bt = "bluetooth";
        try {
            btFolder = folderSearchBT(root, bt);
        } catch (FileNotFoundException e) {
            Log.e("FILE: ", e.getMessage());
        }

        for (int i = 0; i < btFolder.size(); i++) {

            String g = btFolder.get(i).toString();

            String[] subf = g.split(splitchar);

            String s = subf[subf.length - 1].toUpperCase();

            boolean equals = s.equalsIgnoreCase(bt);

            if (equals)
                return g;
        }
        return null; // not found
    }
}
