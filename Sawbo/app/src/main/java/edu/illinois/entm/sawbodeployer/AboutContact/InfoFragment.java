package edu.illinois.entm.sawbodeployer.AboutContact;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
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
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position==0){
                    about.setTextColor(getResources().getColor(R.color.black));contact.setTextColor(getResources().getColor(R.color.gray_text));
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
        }

        @Override
        public Fragment getItem(int pos) {

            switch(pos) {

                case 0: {
                    return AboutFragment.newInstance();
                }
                case 1:{
                    return ContactFragment.newInstance();
                }
                default: return AboutFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private void switchColor(final TextView textView, Integer from, Integer to){
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), from, to);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((Integer)animator.getAnimatedValue());
            }

        });
        colorAnimation.start();
    }
}
