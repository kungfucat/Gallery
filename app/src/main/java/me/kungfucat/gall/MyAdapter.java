package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    String bucket;

    public MyAdapter(Context context, List<ImageModel> list, String bucket) {
        data = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        selectedIds = new boolean[list.size() + 10];
        this.bucket = bucket;
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
        }

        //if is a video
        else if (data.get(position).getAVideo()) {

            GlideApp.with(context)
                    .asBitmap()
                    .override(90, 90)
                    .load(data.get(position).getUrl())
                    .placeholder(new ColorDrawable(Color.parseColor("#000000")))
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

//    class BuildBitmaps extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected Void doInBackground(String... strings) {
//            File file = new File(strings[0]);
//            Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//            return null;
//        }
//
//
//    }
}
