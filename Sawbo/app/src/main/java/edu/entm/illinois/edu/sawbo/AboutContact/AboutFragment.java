package edu.entm.illinois.edu.sawbo.AboutContact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.entm.illinois.edu.sawbo.R;

/**
 * Created by Mahsa on 4/29/2017.
 */

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_fragment, container, false);

       // TextView tv = (TextView) v.findViewById(R.id.tvFragFirst);
        //tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static AboutFragment newInstance() {

        AboutFragment f = new AboutFragment();


        return f;
    }
}
