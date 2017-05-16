package edu.illinois.entm.sawbodeployer.VideoLibrary;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import edu.illinois.entm.sawbodeployer.sawbo.R;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.viewHolder> {

    Video object = new Video();
    Context context;
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


    public VideoListAdapter(Video object, Context context) {
        this.object = object;
        this.context = context;
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
        Glide.with(context)
                .load("http://sawbo-illinois.org/images/videoThumbnails/"+video.getImage())
                .into(holder.videoImage);
    }

    @Override
    public int getItemCount() {
        return object.getAll().size();
    }

}
