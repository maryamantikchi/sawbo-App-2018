package sawbodeployer.entm.illinois.edu.sawbo.VideoLibrary;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rey.material.widget.RelativeLayout;

import sawbodeployer.entm.illinois.edu.sawbo.R;

/**
 * Created by Mahsa on 4/4/2017.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.viewHolder> {

    Video object = new Video();

    Bitmap thumbnail;
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


    public VideoListAdapter(Video object){
        this.object = object;
        for (int i=0;i<this.object.getAll().size();i++){
            System.err.println(this.object.getAll().get(i).getFilename());
            if (this.object.getAll().get(i).getFilename().contains("Light")||this.object.getAll().get(i).getFilename().contains("LIGHT")){
                this.object.getAll().remove(this.object.getAll().get(i));
                }
        }

    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final viewHolder holder, int position) {
        final Video.all video = object.getAll().get(position);
        holder.videoTitle.setText(video.getVideo());
        holder.videoImage.setImageBitmap(thumbnail);
    }

    @Override
    public int getItemCount() {
        return object.getAll().size();
    }


}
