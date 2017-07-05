package edu.illinois.entm.sawbodeployer.MyVideoDetail;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;

import at.blogc.android.views.ExpandableTextView;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.ShareVideoFragment;
import edu.illinois.entm.sawbodeployer.VideoDB.MyVideoDataSource;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import uk.co.jakelee.vidsta.VidstaPlayer;
import uk.co.jakelee.vidsta.listeners.FullScreenClickListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;

/**
 * Created by Mahsa on 5/11/2017.
 */

public class MyVideoDetailFragment extends android.support.v4.app.Fragment{
    View view;
    public all videoDetail;
    Typeface title_font,title_video_font,title_religion_font;
    TextView title, myVideo,videoName,religion;
    ExpandableTextView expandableTextView;
    ImageButton buttonToggle;
    String videoPath;
    Button select_language;
    MyVideoDataSource dataSource;
    RelativeLayout groupLayput;


    public MyVideoDetailFragment(){
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_detail, container, false);
        initialize();

        return view;
    }

    private void initialize(){

        Button deleteBtn = (Button) view.findViewById(R.id.download_video);
        deleteBtn.setText("DELETE");

        groupLayput = (RelativeLayout)view.findViewById(R.id.my_video_detail_layout);



        select_language = (Button)view.findViewById(R.id.select_video_language);
        select_language.setVisibility(View.GONE);

        title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        title_video_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        title_religion_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf");

        title = (TextView)view.findViewById(R.id.title_video_detail);
        title.setTextColor(getResources().getColor(R.color.gray_text));
        title.setTypeface(title_font);


        myVideo = (TextView)view.findViewById(R.id.title_my_video_detail);
        myVideo.setTypeface(title_font);

        videoName = (TextView)view.findViewById(R.id.title_video_name);
        videoName.setText(videoDetail.getTitle());
        videoName.setTypeface(title_video_font);

        religion = (TextView)view.findViewById(R.id.title_religion_video_detail);
        religion.setTypeface(title_religion_font);
        religion.setText(videoDetail.getLanguage()+" from "+videoDetail.getCountry());

        if (videoDetail.getGp_file().length()==0)
        videoPath = getActivity().getFilesDir() + "/" + videoDetail.getVideolight();
        else videoPath = getActivity().getFilesDir()+ "/" + videoDetail.getGp_file();

        expandableTextView = (ExpandableTextView) view.findViewById(R.id.expandable_txt_video_detail);
        expandableTextView.setText(videoDetail.getDescription());
        expandableTextView.setTypeface(title_religion_font);
        expandableTextView.setInterpolator(new OvershootInterpolator());
        expandableTextView.setExpandInterpolator(new OvershootInterpolator());
        expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

        buttonToggle = (ImageButton) view.findViewById(R.id.button_toggle_video_detail);

        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                expandableTextView.toggle();
            }
        });




        final VidstaPlayer player = (VidstaPlayer)view.findViewById(R.id.player);
        player.setVideoSource(videoPath);
        player.setAutoLoop(false);
        player.setAutoPlay(false);

        player.setOnFullScreenClickListener(new FullScreenClickListener() {
            @Override
            public void onToggleClick(boolean isFullscreen) {
                if (isFullscreen){
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) player
                            .getLayoutParams();
                    params.width =  metrics.widthPixels;
                    params.height = metrics.heightPixels;
                    params.leftMargin = 0;
                    player.setLayoutParams(params);
                }else {
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams)
                            player.getLayoutParams();
                    params.width = metrics.widthPixels;
                    params.height = (int) (250*metrics.density);
                    player.setLayoutParams(params);
                }

            }
        });

        player.setOnVideoFinishedListener(new VideoStateListeners.OnVideoFinishedListener() {
            @Override
            public void OnVideoFinished(VidstaPlayer evp) {
                if (player.isFullScreen()){
                    DisplayMetrics metrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams)
                            player.getLayoutParams();
                    params.width = metrics.widthPixels;
                    params.height = (int) (250*metrics.density);
                    player.setLayoutParams(params);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this video?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                deleteVideo();
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

        Button share = (Button)view.findViewById(R.id.share_video);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // toggleView(false);

                enableDisableViewGroup(groupLayput,false);

                ShareVideoFragment fragment = new ShareVideoFragment();
                if (videoDetail.getGp_file().length()==0)
                    fragment.videoPath = videoDetail.getVideolight();
                else if (videoDetail.getVideolight().length()==0)fragment.videoPath = videoDetail.getGp_file();

                fragment.videoFile = videoDetail;

                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction().replace(R.id.main_container, fragment)
                        .addToBackStack(null).commit();
            }
        });

    }

    private void deleteVideo(){
        dataSource = new MyVideoDataSource(getContext());
        dataSource.open();

        boolean isLight = false;

        String filename = "";
        if (videoDetail.getGp_file() == null || videoDetail.getGp_file().equals("")){
            isLight = true;
            filename = videoDetail.getVideolight();
        }else if(videoDetail.getVideolight() == null || videoDetail.getVideolight().equals("")){
            isLight = false;
            filename = videoDetail.getGp_file();
        }

        File file = new File(getActivity().getFilesDir() + "/" + filename);
        file.delete();

        File root = Environment.getExternalStorageDirectory();
        File Dir=null;
        Dir = new File(root.getAbsolutePath() +"/.Sawbo/Images");
        String icon = videoDetail.getImage();
        if (isLight) icon += "_light";
        File fileImg = new File(Dir, icon);


        if (!fileImg.exists()) {
            fileImg.delete();
        }

        if (isLight){
            dataSource.deleteVideoLight(videoDetail);
        }else {
            dataSource.deleteVideoStandard(videoDetail);
        }

        dataSource.close();
    }

    @Override
    public void onResume() {
        super.onResume();
       // toggleView(true);
        enableDisableViewGroup(groupLayput,true);
    }



   /* private void toggleView(Boolean isEnable){
        for (int i = 0; i < groupLayput.getChildCount(); i++) {
            View child = groupLayput.getChildAt(i);
            child.setEnabled(isEnable);
            child.setClickable(isEnable);
        }
    }*/


    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

}
