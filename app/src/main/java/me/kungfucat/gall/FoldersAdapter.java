package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

/**
 * Created by harsh on 12/14/17.
 */

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FolderViewHolder> {

    LayoutInflater inflater;
    Context context;
    ArrayList<FoldersModel> foldersModelList = null;

    public FoldersAdapter(Context context, ArrayList<FoldersModel> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        foldersModelList = list;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.folder_custom_row, parent, false);
        FolderViewHolder holder = new FolderViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {

        GlideApp.with(context)
                .load(foldersModelList.get(position).getImageModelsList().get(0).url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(200, 200)
                .thumbnail(0.5f)
                .placeholder(new ColorDrawable(Color.BLACK))
                .into(holder.foldersImageView);

        String textToShow=foldersModelList.
                get(position).
                getFoldersName()+
                " ("+foldersModelList.get(position).getImageModelsList().size()+
                ")";
        holder.folderName.setText(textToShow);

    }

    @Override
    public int getItemCount() {
        return foldersModelList.size();
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView foldersImageView;
        TextView folderName;

        public FolderViewHolder(View itemView) {
            super(itemView);

            foldersImageView = itemView.findViewById(R.id.foldersImageView);
            folderName = itemView.findViewById(R.id.folderNameTextView);
        }
    }
}

