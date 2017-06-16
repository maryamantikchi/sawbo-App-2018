package edu.illinois.entm.sawbodeployer;

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
import android.widget.Toast;

import com.rey.material.widget.Button;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

                    ApplicationInfo app = getApplicationContext().getApplicationInfo();
                    String filePath = app.sourceDir;

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    // MIME of .apk is "application/vnd.android.package-archive".
                    // but Bluetooth does not accept this. Let's use "*/*" instead.
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                    startActivity(Intent.createChooser(intent, "Share app via"));

          /*     Resources resources = getResources();

                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
                emailIntent.putExtra(Intent.EXTRA_TEXT,"share_email_native");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "share_email_subject");
                emailIntent.setType("message/rfc822");

                PackageManager pm = getContext().getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");


                Intent openInChooser = Intent.createChooser(emailIntent, "share_chooser_text");

                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++) {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email")) {
                        emailIntent.setPackage(packageName);
                    } else if(packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        if(packageName.contains("twitter")) {
                            intent.putExtra(Intent.EXTRA_TEXT, "share_twitter");
                        } else if(packageName.contains("facebook")) {
                            // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                            // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                            // will show the <meta content ="..."> text from that page with our link in Facebook.
                            intent.putExtra(Intent.EXTRA_TEXT, "share_facebook");
                        } else if(packageName.contains("mms")) {
                            intent.putExtra(Intent.EXTRA_TEXT, "share_sms");
                        } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                            intent.putExtra(Intent.EXTRA_TEXT, "share_email_gmail");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "share_email_subject");
                            intent.setType("message/rfc822");
                        }

                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }

                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);
*/




            }
        });


        return view;
    }


}
