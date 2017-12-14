package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import me.kungfucat.gall.MyAppGlideModule;

/**
 * Created by harsh on 12/14/17.
 */

public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.FolderViewHolder> {

    LayoutInflater inflater;
    Context context;
    List<FoldersModel> foldersModelList = Collections.emptyList();

    public FoldersAdapter(Context context, List<FoldersModel> list) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        foldersModelList = list;
//        Log.d("TAG!", String.valueOf(foldersModelList.size()));
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
                .override(200, 200)
                .thumbnail(0.5f)
                .placeholder(new ColorDrawable(Color.BLACK))
                .into(holder.foldersImageView);
        holder.folderName.setText(foldersModelList.get(position).getFoldersName());

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
            folderName= itemView.findViewById(R.id.folderNameTextView);
        }
    }
}

