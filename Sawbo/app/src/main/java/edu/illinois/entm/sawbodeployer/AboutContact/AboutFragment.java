package edu.illinois.entm.sawbodeployer.AboutContact;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.illinois.entm.sawbodeployer.R;


/**
 * Created by Mahsa on 4/29/2017.
 */

public class AboutFragment extends Fragment {
    TextView desc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        desc = (TextView) v.findViewById(R.id.text_about_sawbo);
        desc.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BentonSans Regular.otf"));
        return v;
    }

    public static AboutFragment newInstance() {

        AboutFragment f = new AboutFragment();


        return f;
    }
}
