package sawbodeployer.entm.illinois.edu.sawbo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Mahsa on 4/9/2017.
 */

public class DownloadVideoFragment extends android.support.v4.app.Fragment{
    public DownloadVideoFragment(){
    }
    Button liteFile,standardFile;
    public String standardUrl,liteUrl;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.fragment_download_video, container, false);

        return view;
    }
    void initialize(){
        liteFile = (Button)view.findViewById(R.id.btn_download_lite);
        standardFile = (Button)view.findViewById(R.id.btn_download_standard);
        liteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        standardFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
