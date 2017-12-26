package me.kungfucat.gall;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import me.kungfucat.gall.interfaces.OnItemClickListener;

public class SingleFolderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<ImageModel> imageModelsList = null;
    ArrayList<String> selectedFilePaths;

    boolean[] selectedPositions;

    Toolbar toolbar, selectionToolbar;
    Context context;
    boolean isInSelectionMode = false;
    int numbersSelected = 0;
    boolean isAVideo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_single_folder);

        toolbar = findViewById(R.id.toolBarForSingleFolder);
        selectionToolbar = findViewById(R.id.selectionToolBar);
        recyclerView = findViewById(R.id.recyclerViewForAllImages);
        context = this;

        selectedFilePaths = new ArrayList<>();
        imageModelsList = new ArrayList<>();

        imageModelsList = getIntent().getParcelableArrayListExtra("data");
        selectedPositions = new boolean[imageModelsList.size() + 10];
        String title = getIntent().getStringExtra("bucket");
        if (title == null || title.equals("")) {
            title = getResources().getString(R.string.app_name);
        }

        final TextView toolbarTextView = findViewById(R.id.singleFolderToolbarTitle);
        toolbarTextView.setText(title);
        toolbarTextView.setTextColor(Color.parseColor("#000000"));
        toolbarTextView.setTextColor(Color.parseColor("#ffffff"));

        final Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        final ImageView toolBarBackArrow = findViewById(R.id.singleFoldertoolbarBackArrow);

        final String finalTitle = title;
        toolBarBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(50);
                finish();
            }
        });


        if (imageModelsList.isEmpty()) {
            finish();
        }
        adapter = new MyAdapter(this, imageModelsList, title);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        SlideInLeftAnimationAdapter animationAdapter = new SlideInLeftAnimationAdapter(adapter);
        recyclerView.setAdapter(animationAdapter);

        try {
            if (imageModelsList.get(0).getAVideo()) {
                isAVideo = true;
            }
        } catch (Exception e) {

        }

        ImageView selectionBackArrow = findViewById(R.id.selectionBackArrow);
        ImageView selectionShareIcon = findViewById(R.id.shareSelectedIcon);
        ImageView selectionDeleteIcon = findViewById(R.id.deleteSelectedIcon);

        final TextView selectionTextTitle = findViewById(R.id.selectedTextTitle);

        recyclerView.addOnItemTouchListener(new ImageClickedListener(this, recyclerView, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!isInSelectionMode) {
                    //if is a video, start another activity
                    if (imageModelsList.get(position).getAVideo()) {
                        Intent intent = new Intent(getApplicationContext(), ShowDetailsVideoActivity.class);
                        intent.putParcelableArrayListExtra("data", imageModelsList);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                    //if not a video, load the normal activity
                    else {
                        Intent intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
                        intent.putParcelableArrayListExtra("data", imageModelsList);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                } else {
                    if (selectedPositions[position]) {
                        selectedPositions[position] = false;
                        numbersSelected--;
                        if (numbersSelected == 0) {
                            isInSelectionMode = false;
                            selectionToolbar.setVisibility(View.GONE);
                            toolbar.setVisibility(View.VISIBLE);
                        } else {
                            selectionTextTitle.setText(numbersSelected + " Selected");
                        }
                    } else {
                        selectedPositions[position] = true;
                        numbersSelected++;
                        selectionTextTitle.setText(numbersSelected + " Selected");

                    }
                    adapter.setSelectedIds(selectedPositions);
                }
            }


            @Override
            public void onItemLongClick(View view, int position) {
                if (!isInSelectionMode) {
                    vibrator.vibrate(100);
                    isInSelectionMode = true;
                    numbersSelected++;
                    toolbar.setVisibility(View.GONE);
                    selectionToolbar.setVisibility(View.VISIBLE);
                    selectionTextTitle.setText("1 Selected");
                    selectedPositions[position] = true;
                    adapter.setSelectedIds(selectedPositions);
                }
            }
        }));
        selectionBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(50);
                numbersSelected = 0;
                isInSelectionMode = false;
                selectionToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                Arrays.fill(selectedPositions, false);
                adapter.setSelectedIds(selectedPositions);
            }
        });

        selectionShareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(50);

                ArrayList<Uri> files = new ArrayList<>();
                for (int i = 0; i < imageModelsList.size() + 5; i++) {
                    if (selectedPositions[i]) {
                        File file = new File(imageModelsList.get(i).getUrl());
                        Uri uri = Uri.fromFile(file);
                        files.add(uri);
                    }
                }
                numbersSelected = 0;
                isInSelectionMode = false;
                selectionToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                Arrays.fill(selectedPositions, false);
                adapter.setSelectedIds(selectedPositions);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.setType("*/*");

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                startActivity(intent);
            }
        });

        selectionDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(50);

                new MaterialStyledDialog
                        .Builder(context)
                        .setTitle("Delete")
                        .setStyle(Style.HEADER_WITH_TITLE)
                        .setHeaderColor(R.color.black)
                        .withDarkerOverlay(true)
                        .setCancelable(true)
                        .setPositiveText("Yes")
                        .withDivider(true)
                        .setDescription("Are you sure you want to delete the " + numbersSelected + " images/videos?")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ArrayList<Integer> positionsToDelete = new ArrayList<>();
                                for (int i = imageModelsList.size() - 1; i >= 0; i--) {
                                    if (selectedPositions[i]) {
                                        positionsToDelete.add(i);
                                    }
                                }

                                int a = 0;
                                for (int i = 0; i < positionsToDelete.size(); i++) {
                                    int pos = positionsToDelete.get(i);
                                    File file = new File(imageModelsList.get(pos).getUrl());
                                    if (file.delete()) {
                                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                        imageModelsList.remove(pos);
                                    } else {
                                        a = 1;
                                    }
                                }
                                if (a == 0) {
                                    Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                }
                                numbersSelected = 0;
                                isInSelectionMode = false;
                                selectionToolbar.setVisibility(View.GONE);
                                toolbar.setVisibility(View.VISIBLE);
                                Arrays.fill(selectedPositions, false);
                                adapter.setSelectedIds(selectedPositions);

                                if (imageModelsList.size() == 0) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .setNegativeText("No")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();
            }

        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }

    @Override
    public void onBackPressed() {

        if (!isInSelectionMode) {
            finish();
        } else {
            numbersSelected = 0;
            isInSelectionMode = false;
            selectionToolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            Arrays.fill(selectedPositions, false);
            adapter.setSelectedIds(selectedPositions);
        }
    }
}

