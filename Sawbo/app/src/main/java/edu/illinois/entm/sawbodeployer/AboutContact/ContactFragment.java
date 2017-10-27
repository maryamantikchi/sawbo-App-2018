package edu.illinois.entm.sawbodeployer.AboutContact;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import edu.illinois.entm.sawbodeployer.R;

/**
 * Created by Mahsa on 4/29/2017.
 */

public class ContactFragment extends android.support.v4.app.Fragment implements View.OnClickListener{
    RelativeLayout mail,facebook,twitter,youtube,sawbo;
    Button rate_btn;
    View v;

    public ContactFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.contact_fragment, container, false);

        initialize();

        return v;
    }

 /*   public static ContactFragment newInstance() {

        ContactFragment f = new ContactFragment();
        return f;
    }*/

    private void initialize(){
        mail = (RelativeLayout) v.findViewById(R.id.contact_email);
        mail.setOnClickListener(this);
        facebook = (RelativeLayout) v.findViewById(R.id.contact_facebook);
        facebook.setOnClickListener(this);
        twitter = (RelativeLayout) v.findViewById(R.id.contact_twitter);
        twitter.setOnClickListener(this);
        youtube = (RelativeLayout) v.findViewById(R.id.contact_youtube);
        youtube.setOnClickListener(this);
        sawbo = (RelativeLayout) v.findViewById(R.id.contact_sawbo);
        sawbo.setOnClickListener(this);
        rate_btn = (Button)v.findViewById(R.id.rate_google_play);
        rate_btn.setOnClickListener(this);
        Typeface btn_font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Medium.otf");
        rate_btn.setTypeface(btn_font);
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        String url;
        switch (v.getId())
        {
            case R.id.contact_email:
                 intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",getResources().getString(R.string.contact_mail), null));
                getActivity().startActivity(Intent.createChooser(intent, "Send email..."));
                break;

            case R.id.contact_facebook:
                url = getResources().getString(R.string.contact_facebook);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
                break;

            case R.id.contact_sawbo:
                url = getResources().getString(R.string.contact_sawbo);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
                break;

            case R.id.contact_twitter:
                url = getResources().getString(R.string.contact_twitter);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
                break;

            case R.id.contact_youtube:
                url = getResources().getString(R.string.contact_youtube);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getActivity().startActivity(intent);
                break;

            case R.id.rate_google_play:
                Uri uri = Uri.parse("market://details?id=" + getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                }
                break;

        }
    }
}

