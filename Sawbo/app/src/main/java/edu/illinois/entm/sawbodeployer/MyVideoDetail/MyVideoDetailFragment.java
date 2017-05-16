package edu.illinois.entm.sawbodeployer.MyVideoDetail;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import at.blogc.android.views.ExpandableTextView;
import edu.illinois.entm.sawbodeployer.R;
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

        if (videoDetail.getVideo().length()==0)
        videoPath = getActivity().getFilesDir() + "/" + videoDetail.getVideolight();
        else videoPath = getActivity().getFilesDir()+ "/" + videoDetail.getVideo();

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
                dataSource = new MyVideoDataSource(getContext());
                dataSource.open();


           /*     File dirFiles = getActivity().getFilesDir();
                final ArrayList<String> videoFile = new ArrayList<String>();

                for (String strFile : dirFiles.list()){
                    String extension = strFile.substring(strFile.lastIndexOf(".")+1);
                    if(extension.equals("3gp") || extension.equals("mp4")){
                        videoFile.add(strFile);
                        for (int i=0;i<dataSource.findDownloadVideos(strFile).size();i++){
                            videos.add(dataSource.findDownloadVideos(strFile).get(i));
                        }
                    }*/

                dataSource.deleteVideo(videoDetail);
                dataSource.close();

            }
        });

    }
}
