package edu.illinois.entm.sawbodeployer.VideoLibrary;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.illinois.entm.sawbodeployer.R;
import edu.illinois.entm.sawbodeployer.VideoDetail.VideoDetailFragment;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.viewHolder> {

    private FragmentManager manager;
    Video object = new Video();
    Context context;
    VideoLibraryFragment fragment;
    public class viewHolder extends RecyclerView.ViewHolder {

        public ImageView videoImage;
        public RelativeLayout layout;
        public TextView videoTitle;


        public viewHolder(View itemView) {
            super(itemView);
            videoImage = (ImageView) itemView.findViewById(R.id.image_video_item);
            layout = (RelativeLayout) itemView.findViewById(R.id.view_video_item);
            videoTitle = (TextView) itemView.findViewById(R.id.title_video_item);
        }
    }


    public VideoListAdapter(Video object, Context context, FragmentManager manager, VideoLibraryFragment fragment) {
        this.object = object;
        this.context = context;
        this.manager = manager;
        this.fragment = fragment;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        final all video = object.getAll().get(position);
        holder.videoTitle.setText(video.getTitle());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoDetailFragment newfragment = new VideoDetailFragment();
                newfragment.videoDetail = video;
                newfragment.video = object;

                manager.beginTransaction().hide(fragment)
                        .add(R.id.main_container, newfragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });
        Glide.with(context)
                .load("http://sawbo-illinois.org/images/videoThumbnails/"+video.getImage())
                .into(holder.videoImage);
    }

    @Override
    public int getItemCount() {
        return object.getAll().size();
    }

}
