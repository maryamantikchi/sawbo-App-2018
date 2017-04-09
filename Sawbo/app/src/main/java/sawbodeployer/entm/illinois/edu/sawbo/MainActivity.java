package sawbodeployer.entm.illinois.edu.sawbo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.rey.material.widget.ImageButton;

import sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary.VideoLibraryFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private ImageButton home,video_library,my_videos,share,info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

    }

    public void initialize(){
        home = (ImageButton)findViewById(R.id.btn_home);
        home.setOnClickListener(this);
        video_library = (ImageButton)findViewById(R.id.btn_video_library);
        video_library.setOnClickListener(this);
        my_videos = (ImageButton)findViewById(R.id.btn_my_video);
        my_videos.setOnClickListener(this);
        share = (ImageButton)findViewById(R.id.btn_share);
        share.setOnClickListener(this);
        info = (ImageButton)findViewById(R.id.btn_info);
        info.setOnClickListener(this);


        fragmentManager = getSupportFragmentManager();
        fragment = new HomeFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_home:
                fragment = new HomeFragment();
                break;
            case R.id.btn_video_library:
                fragment = new VideoLibraryFragment();
                break;
            case R.id.btn_my_video:
                fragment = new MyVideoFragment();
                break;
            case R.id.btn_share:
                break;
            case R.id.btn_info:
                fragment = new InfoFragment();
                break;
            //handle multiple view click events
        }
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();
    }
}
