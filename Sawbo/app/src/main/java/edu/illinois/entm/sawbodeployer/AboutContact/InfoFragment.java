package edu.illinois.entm.sawbodeployer.AboutContact;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.illinois.entm.sawbodeployer.R;

/**
 * Created by Mahsa on 4/1/2017.
 */
public class
InfoFragment extends android.support.v4.app.Fragment {
    View view;
    TextView about,contact;

    InfoAdapter adapter;



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
        final VerticalViewPager pager = (VerticalViewPager) view.findViewById(R.id.viewPager);
        adapter = new InfoAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==0){
                    about.setTextColor(getResources().getColor(R.color.black));
                    contact.setTextColor(getResources().getColor(R.color.gray_text));

                }else {
                    contact.setTextColor(getResources().getColor(R.color.black));
                    about.setTextColor(getResources().getColor(R.color.gray_text));
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Typeface titleFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/BebasNeue.otf");

        about = (TextView)view.findViewById(R.id.title_about_setting);
        about.setTypeface(titleFont);
        contact = (TextView)view.findViewById(R.id.title_contact_setting);
        contact.setTypeface(titleFont);
    }


    private class InfoAdapter extends FragmentPagerAdapter {

        public InfoAdapter(FragmentManager fm) {
            super(fm);
            System.err.println("miad inja1");

        }

        @Override
        public Fragment getItem(int pos) {
            System.err.println("miad inja2");
            switch(pos) {
                case 0: {
                    Fragment about = new AboutFragment();
                    return about;
                }
                case 1:{
                    Fragment cf = new ContactFragment();
                    return cf;//ContactFragment.newInstance();
                }
                default:{
                    Fragment about = new AboutFragment();
                    return about;
                }
            }
        }

        @Override
        public int getCount() {
            System.err.println("miad inja3");

            return 2;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
