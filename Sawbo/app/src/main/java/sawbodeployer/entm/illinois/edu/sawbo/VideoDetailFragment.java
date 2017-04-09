package sawbodeployer.entm.illinois.edu.sawbo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;

import com.rey.material.widget.TextView;

import at.blogc.android.views.ExpandableTextView;
import sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary.Video;
import uk.co.jakelee.vidsta.VidstaPlayer;
import uk.co.jakelee.vidsta.listeners.FullScreenClickListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoDetailFragment extends android.support.v4.app.Fragment {
    Typeface title_font,title_video_font;
    public Video.all videoDetail;
    TextView title;
    String videoPath;
    public String light_url;
    View view;
    Button download,select_language,share;

     ExpandableTextView expandableTextView;
    ImageButton buttonToggle;

    public VideoDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_video_detail, container, false);
        initialize();

        expandableTextView.setAnimationDuration(1000L);

        expandableTextView.setInterpolator(new OvershootInterpolator());

        expandableTextView.setExpandInterpolator(new OvershootInterpolator());
        expandableTextView.setCollapseInterpolator(new OvershootInterpolator());

        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                expandableTextView.toggle();
            }
        });
        buttonToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                buttonToggle.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void initialize(){
        title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        title_video_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        title = (TextView)view.findViewById(R.id.title_video_detail);
        title.setTypeface(title_font);
        videoPath = getContext().getResources().getString(R.string.video_url)+videoDetail.getFilename();

        expandableTextView = (ExpandableTextView) view.findViewById(R.id.expandable_txt_video_detail);
        buttonToggle = (ImageButton) view.findViewById(R.id.button_toggle_video_detail);

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

        download = (Button)view.findViewById(R.id.download_video);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadVideoFragment fragment = new DownloadVideoFragment();
                fragment.liteUrl = light_url;
                fragment.standardUrl = videoDetail.getFilename();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.main_container, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });


    }



}
