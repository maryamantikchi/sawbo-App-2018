package edu.illinois.entm.sawbodeployer.MyVideos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.entm.sawbodeployer.MyVideoDetail.MyVideoDetailFragment;
import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.VideoLibrary.all;

/**
 * Created by Mahsa on 5/5/2017.
 */

public class MyVideoListAdapter extends RecyclerView.Adapter<MyVideoListAdapter.viewHolder>  {
    List<all> object = new ArrayList<all>();
    Context context;

    boolean isSelectedAll = false;

    List<all> selectedVideo;

    public class viewHolder extends RecyclerView.ViewHolder {

        public ImageView videoImage;
        public RelativeLayout layout;
        public TextView videoTitle;
        public CheckBox selector;
        LinearLayout layout_selector;



        public viewHolder(View itemView) {
            super(itemView);
            videoImage = (ImageView) itemView.findViewById(R.id.image_my_video_item);
            layout = (RelativeLayout) itemView.findViewById(R.id.view_my_video_item);
            videoTitle = (TextView) itemView.findViewById(R.id.title_my_video_item);
            selector = (CheckBox) itemView.findViewById(R.id.select_my_video_checkbox);
            layout_selector = (LinearLayout)itemView.findViewById(R.id.select_my_video_item);
        }
    }


    public MyVideoListAdapter(List<all> object, Context context) {
        this.object = object;
        this.context = context;
        selectedVideo = new ArrayList<>();
    }


    @Override
    public MyVideoListAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_video_list_item, parent, false);

        return new MyVideoListAdapter.viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyVideoListAdapter.viewHolder holder, int position) {
        final all video = object.get(position);

        holder.videoTitle.setText(video.getTitle());
      //  holder.videoImage.setImageBitmap(readImage(video));

        String path = null;
        if (!video.getLite_file().contains("Bluetooth") && !video.getGp_file().contains("Bluetooth")) {

            if (video.getGp_file().length() == 0)
                path = context.getFilesDir() + "/" + video.getVideolight();
            else path = context.getFilesDir() + "/" + video.getGp_file();
        } else path = video.getVideolight();

        Glide.with(context)
                .load(path) // or URI/path
                .into(holder.videoImage);
        holder.layout_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.selector.performClick();
            }
        });

        holder.selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !isSelectedAll){
                    isSelectedAll = false;
                    selectedVideo.add(video);

                }else if(!isChecked){
                    isSelectedAll = false;
                    selectedVideo.remove(video);
                }
            }
        });

        if (isSelectedAll) {
            holder.selector.setChecked(true);
            selectedVideo.add(video);
        }/*else if (!isSelectedAll) {
            holder.selector.setChecked(false);
            selectedVideo.remove(video);

        }*/

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyVideoDetailFragment fragment = new MyVideoDetailFragment();
                    fragment.videoDetail = video;

                    MyVideoFragment myVideoFragment = new MyVideoFragment();

                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().hide(myVideoFragment)
                            .add(R.id.main_container, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            });
    }

    @Override
    public int getItemCount() {
        return object.size();
    }

    Bitmap readImage(all video){
        File root = Environment.getExternalStorageDirectory();
        File Dir = new File(root.getAbsolutePath() +"/.Sawbo/Images");
        if (Dir.exists()) System.err.println("exit");
        File file = new File(Dir, video.getImage());

        if (file.exists()) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
            return bitmap;
        }else {
            String path = null;
            if (!video.getLite_file().contains("Bluetooth") && !video.getGp_file().contains("Bluetooth")){
                if (video.getGp_file().length()==0)
                    path = context.getFilesDir() + "/" + video.getVideolight();
                else path = context.getFilesDir()+ "/" + video.getGp_file();
            }else {
                path = video.getGp_file();
            }
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            System.err.println(thumb+" thumy");
            return thumb;
        }
    }

    public void selectAll(Boolean isSelectedAll){

        this.isSelectedAll=isSelectedAll;
        notifyDataSetChanged();
    }

    public List<all> getSelectedVideo(){

        return selectedVideo;
    }

}
