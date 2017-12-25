package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
    boolean[] selectedIds;

    public MyAdapter(Context context, List<ImageModel> list) {
        data = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        selectedIds = new boolean[list.size() + 10];
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView checkImageView = holder.checkImageView;
        ImageView previewImageView = holder.imageView;

        if (!data.get(position).getAVideo()) {
            GlideApp.with(context)
                    .asBitmap()
                    .load(data.get(position).getUrl())
                    .override(200, 200)
                    .thumbnail(0.5f)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(previewImageView);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            checkImageView.setLayoutParams(layoutParams);
        }

        //if is a video
        else if (data.get(position).getAVideo()) {

            File file = new File(data.get(position).getUrl());
            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);

            GlideApp.with(context)
                    .asBitmap()
                    .override(100, 100)
                    .load(bMap)

                    .into(previewImageView);

            ImageView playIcon = holder.playIcon;
            playIcon.setVisibility(View.VISIBLE);
        }

        if (selectedIds[position]) {
            checkImageView.setVisibility(View.VISIBLE);
        } else {
            checkImageView.setVisibility(View.GONE);
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

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            playIcon = itemView.findViewById(R.id.action_play_icon);
            checkImageView = itemView.findViewById(R.id.selectedCheckIcon);
        }
    }
}
