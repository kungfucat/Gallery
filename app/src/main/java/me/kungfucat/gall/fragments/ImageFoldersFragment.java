package me.kungfucat.gall.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import me.kungfucat.gall.FoldersAdapter;
import me.kungfucat.gall.FoldersModel;
import me.kungfucat.gall.ImageClickedListener;
import me.kungfucat.gall.R;
import me.kungfucat.gall.SingleFolderActivity;
import me.kungfucat.gall.interfaces.OnItemClickListener;

/**
 * Created by harsh on 12/24/17.
 */

public class ImageFoldersFragment extends Fragment {

    public ImageFoldersFragment() {

    }

    public static ImageFoldersFragment newInstance(int position, ArrayList<FoldersModel> foldersModels) {
        ImageFoldersFragment fragment = new ImageFoldersFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("foldersData", foldersModels);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.imageFoldersRecyclerView);
        Bundle bundle = getArguments();

        final ArrayList<FoldersModel> foldersModel = bundle.getParcelableArrayList("foldersData");
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new FoldersAdapter(getContext(), foldersModel));

        recyclerView.addOnItemTouchListener(new ImageClickedListener(getContext(), new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                Intent intent = new Intent(getContext(), SingleFolderActivity.class);
                intent.putParcelableArrayListExtra("data", foldersModel.get(position).getImageModelsList());
                intent.putExtra("bucket", foldersModel.get(position).getFoldersName());
                startActivity(intent);
            }
        }));

        return view;
    }
}
