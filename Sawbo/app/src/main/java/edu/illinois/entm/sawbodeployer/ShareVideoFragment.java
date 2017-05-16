package edu.illinois.entm.sawbodeployer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by Mahsa on 5/16/2017.
 */

public class ShareVideoFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    View view;
    RelativeLayout sawbo_share,mail_share,facebook_share,twitter_share,
            insta_share,general_share, bluetooth_share,phone_share;
    public String videoPath;

    public ShareVideoFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_share_video, container, false);
        initialize();
        return view;
    }

    private void initialize(){

        sawbo_share = (RelativeLayout)view.findViewById(R.id.share_sawbo);
        sawbo_share.setOnClickListener(this);
        mail_share = (RelativeLayout)view.findViewById(R.id.share_email);
        mail_share.setOnClickListener(this);
        facebook_share = (RelativeLayout)view.findViewById(R.id.share_facebook);
        facebook_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                //shareMedia("com.facebook.katana","com.facebook.katana"+urlEncode());
            }
        });
        twitter_share = (RelativeLayout)view.findViewById(R.id.share_twitter);
        twitter_share.setOnClickListener(this);
        insta_share = (RelativeLayout)view.findViewById(R.id.share_instagram);
        insta_share.setOnClickListener(this);
        general_share = (RelativeLayout)view.findViewById(R.id.share_general);
        general_share.setOnClickListener(this);
        phone_share = (RelativeLayout)view.findViewById(R.id.share_phone);
        phone_share.setOnClickListener(this);
        bluetooth_share = (RelativeLayout)view.findViewById(R.id.share_bluetooth);
        bluetooth_share.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (view.getId())
        {
            case R.id.share_sawbo:

                break;
            case R.id.share_email:

                break;
            case R.id.share_facebook:
                shareMedia("com.facebook.katana","https://twitter.com/intent/tweet?text="+urlEncode());
                break;
            case R.id.share_twitter:

                shareMedia("com.twitter.android","https://twitter.com/intent/tweet?text="+urlEncode());


                break;
            case R.id.share_instagram:

                break;
            case R.id.share_general:

                break;
            case R.id.share_phone:

                break;
            case R.id.share_bluetooth:

                break;
        }

    }

   private void shareMedia(String pacName, String IntentUrl){
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, videoPath);
        tweetIntent.setType("text/plain");

/*        PackageManager packManager = getActivity().getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList){
            if(resolveInfo.activityInfo.packageName.startsWith(pacName)){
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name );
                resolved = true;
                break;
            }
        }
        if(resolved){
            startActivity(tweetIntent);
        }else*/{
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, videoPath);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse(IntentUrl));
            startActivity(i);
        }
    }

 private String urlEncode() {
        try {
            return URLEncoder.encode(videoPath, "UTF-8");
        }catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            return "";
        }
    }
}
