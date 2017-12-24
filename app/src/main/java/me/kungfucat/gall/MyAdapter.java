package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by harsh on 12/13/17.
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<ImageModel> data = Collections.emptyList();

    public MyAdapter(Context context, List<ImageModel> list) {
        data = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GlideApp.with(context)
                .load(data.get(position).getUrl())
                .override(200, 200)
                .thumbnail(0.5f)
                .placeholder(new ColorDrawable(Color.BLACK))
                .into(holder.imageView);

        //if is a video
        if (data.get(position).getAVideo()) {
            ImageView playIcon = holder.playIcon;
            TextView playTimeTextView = holder.playTimeTextView;
            playIcon.setVisibility(View.VISIBLE);
            playTimeTextView.setVisibility(View.VISIBLE);


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.fromFile(new File(data.get(position).getUrl())));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            long seconds = timeInMillisec / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;
            String minutesString = "";
            String secondsString = "";
            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = "" + minutes;
            }
            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = "" + seconds;
            }
            String videoTimeText = minutesString + ":" + secondsString;
            playTimeTextView.setText(videoTimeText);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView playIcon;
        TextView playTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playIcon = itemView.findViewById(R.id.action_play_icon);
            playTimeTextView = itemView.findViewById(R.id.time_of_video);
        }
    }
}
