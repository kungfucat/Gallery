package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by harsh on 12/13/17.
 */


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    LayoutInflater inflater;
    Context context;
    List<ImageModel> data = Collections.emptyList();
    boolean[] selectedIds;

    public MyAdapter(Context context, List<ImageModel> list) {
        data = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        selectedIds=new boolean[list.size()+10];
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    public void setSelectedIds(boolean[] selectedIds) {
        this.selectedIds = selectedIds;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,  int position) {

        if (!data.get(position).getAVideo()) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(data.get(position).getUrl())
                    .override(200, 200)
                    .thumbnail(0.5f)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(holder.imageView);

            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            holder.checkImageView.setLayoutParams(layoutParams);
        }
        //if is a video
        else if (data.get(position).getAVideo()) {

            GlideApp.with(context)
                    .asBitmap()
                    .load(data.get(position).getUrl())
                    .override(100, 100)
                    .thumbnail(0.5f)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(holder.imageView);

            ImageView playIcon = holder.playIcon;
            TextView playTimeTextView = holder.playTimeTextView;
            playIcon.setVisibility(View.VISIBLE);
            playTimeTextView.setVisibility(View.VISIBLE);


            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(context, Uri.fromFile(new File(data.get(position).getUrl())));
            String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

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

        if(selectedIds[position]){
            holder.checkImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.checkImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView playIcon;
        ImageView checkImageView;
        TextView playTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playIcon = itemView.findViewById(R.id.action_play_icon);
            playTimeTextView = itemView.findViewById(R.id.time_of_video);
            checkImageView=itemView.findViewById(R.id.selectedCheckIcon);
        }
    }
}
