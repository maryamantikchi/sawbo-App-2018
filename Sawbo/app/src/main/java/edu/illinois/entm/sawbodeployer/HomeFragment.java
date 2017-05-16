package edu.illinois.entm.sawbodeployer;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rey.material.widget.Button;

import edu.illinois.entm.sawbodeployer.VideoLibrary.VideoLibraryFragment;

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

        Button share_btn = (Button)view.findViewById(R.id.btn_share_home);
        share_btn.setTypeface(btn_font);


        return view;
    }
}
