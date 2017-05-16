package edu.illinois.entm.sawbodeployer.VideoDetail;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import uk.co.jakelee.vidsta.VidstaPlayer;
import uk.co.jakelee.vidsta.listeners.FullScreenClickListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;

import static android.content.ContentValues.TAG;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoDetailFragment extends android.support.v4.app.Fragment {
    Typeface title_font,title_video_font,title_religion_font;
    public all videoDetail;
    TextView title,videoName,religion;
    String videoPath;
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
        return view;
    }

    private void initialize() {
        title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        title_video_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        title_religion_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf");
        title = (TextView)view.findViewById(R.id.title_video_detail);
        title.setTypeface(title_font);

        videoName = (TextView)view.findViewById(R.id.title_video_name);
        videoName.setText(videoDetail.getTitle());
        videoName.setTypeface(title_video_font);

        religion = (TextView)view.findViewById(R.id.title_religion_video_detail);
        religion.setTypeface(title_religion_font);
        religion.setText(videoDetail.getLanguage()+" from "+videoDetail.getCountry());

        videoPath = getContext().getResources().getString(R.string.video_url)+videoDetail.getVideo();

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
        player.setVideoSource(getContext().getResources().getString(R.string.video_url)+videoDetail.getVideo());
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
                fragment.video = videoDetail;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().add(R.id.main_container, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        share = (Button)view.findViewById(R.id.share_video);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, "This is a Test.");
                tweetIntent.setType("text/plain");

                PackageManager packManager = getActivity().getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for(ResolveInfo resolveInfo: resolvedInfoList){
                    if(resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")){
                        tweetIntent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name );
                        resolved = true;
                        break;
                    }
                }
                if(resolved){
                    startActivity(tweetIntent);
                }else{
                    Intent i = new Intent();
                    i.putExtra(Intent.EXTRA_TEXT, "This is a Test.");
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://twitter.com/intent/tweet?text="+urlEncode("This is a Test.")));
                    startActivity(i);
                }


            }
        });

    }
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }

}
