package edu.illinois.entm.sawbodeployer.VideoDetail;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import at.blogc.android.views.ExpandableTextView;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.ShareVideoFragment;
import edu.illinois.entm.sawbodeployer.VideoLibrary.Video;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;
import uk.co.jakelee.vidsta.VidstaPlayer;
import uk.co.jakelee.vidsta.listeners.FullScreenClickListener;
import uk.co.jakelee.vidsta.listeners.VideoStateListeners;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoDetailFragment extends android.support.v4.app.Fragment {
    Typeface title_font,title_video_font,title_religion_font;
    public all videoDetail;
    public Video video;
    TextView title,videoName,religion,my_video;
    String videoPath;
    View view;
    Button download,select_language,share;
    RelativeLayout groupLayput;

     ExpandableTextView expandableTextView;
    ImageButton buttonToggle;

    ScrollView scroll;

    public VideoDetailFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_video_detail, container, false);
        initialize();
        return view;
    }

    private void initialize() {
        my_video = (TextView)view.findViewById(R.id.title_my_video_detail);
        my_video.setVisibility(View.GONE);
        groupLayput = (RelativeLayout)view.findViewById(R.id.my_video_detail_layout);
        title_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");
        title_video_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        title_religion_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf");
        title = (TextView)view.findViewById(R.id.title_video_detail);
        title.setTypeface(title_font);

        scroll = (ScrollView) view.findViewById(R.id.scroll_detail);

        videoName = (TextView)view.findViewById(R.id.title_video_name);
        videoName.setText(videoDetail.getTitle());
        videoName.setTypeface(title_video_font);

        religion = (TextView)view.findViewById(R.id.title_religion_video_detail);
        religion.setTypeface(title_religion_font);
        religion.setText(videoDetail.getLanguage()+" from "+videoDetail.getCountry());

        if (videoDetail.getLite_file().equals("")||videoDetail.getLite_file()==null)
        videoPath = getContext().getResources().getString(R.string.video_url)+videoDetail.getGp_file();
        else videoPath = getContext().getResources().getString(R.string.video_url)+videoDetail.getLite_file();


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

        select_language = (Button)view.findViewById(R.id.select_video_language);
        select_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAlertDialogWithListview();
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

        download = (Button)view.findViewById(R.id.download_video);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enableDisableViewGroup(groupLayput,false);
                DownloadVideoFragment fragment = new DownloadVideoFragment();
                fragment.video = videoDetail;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_container, fragment)
                        .addToBackStack(null).commit();
            }
        });

        share = (Button)view.findViewById(R.id.share_video);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enableDisableViewGroup(groupLayput,false);

                ShareVideoFragment fragment = new ShareVideoFragment();
                fragment.videoPath = videoPath;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_container, fragment)
                        .addToBackStack(null).commit();
                        /*.addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)*/
                        //.commit();

             /*   Intent tweetIntent = new Intent(Intent.ACTION_SEND);
                tweetIntent.putExtra(Intent.EXTRA_TEXT, videoPath);
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
                }*/


            }
        });

    }
  /*  private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }*/

    private void ShowAlertDialogWithListview() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());

        builderSingle.setTitle("Select Language");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.select_dialog_singlechoice,video.getLanguage());

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String language = arrayAdapter.getItem(which);
                all selected_video = null;

                for (int i=0;i<video.getAll().size();i++){
                    if ((videoDetail.getTopic().equals(video.getAll().get(i).getTopic()))
                            && (video.getAll().get(i).getLanguage().equals(language))){
                      selected_video = video.getAll().get(i);
                        break;
                    }
                }

                if (selected_video!=null){
                    VideoDetailFragment fragment = new VideoDetailFragment();
                    fragment.videoDetail = selected_video;
                    fragment.video = video;
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(fragment).attach(fragment).commit();
                }else {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                    builderInner.setMessage("Not found "+videoDetail.getTopic()+" in "+language);
                    builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.dismiss();
                        }
                    });
                    builderInner.show();
                }



            }
        });
        builderSingle.show();

    }


    public void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // toggleView(true);
        enableDisableViewGroup(groupLayput,true);
    }

}
