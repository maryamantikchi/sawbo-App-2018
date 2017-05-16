package edu.illionois.entm.sawbodeployer.sawbo.AboutContact;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.illionois.entm.sawbodeployer.sawbo.R;

/**
 * Created by Mahsa on 4/1/2017.
 */
public class
InfoFragment extends android.support.v4.app.Fragment {
    View view;

    public InfoFragment(){
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        view = inflater.inflate(R.layout.fragment_info, container, false);

        initialize();

        return view;
    }

    private void initialize(){
        VerticalViewPager pager = (VerticalViewPager) view.findViewById(R.id.viewPager);
        pager.setAdapter(new InfoAdapter(getActivity().getSupportFragmentManager()));
    }


    private class InfoAdapter extends FragmentPagerAdapter {

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return AboutFragment.newInstance();
                case 1: return ContactFragment.newInstance();
                default: return AboutFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
