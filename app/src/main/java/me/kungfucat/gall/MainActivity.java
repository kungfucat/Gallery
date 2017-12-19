package me.kungfucat.gall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import me.kungfucat.gall.interfaces.OnItemClickListener;

public class MainActivity extends AppCompatActivity {
    //TODO: Remove the meta-data from manifest and re-enable the crash analytics
    RecyclerView recyclerView;
    FoldersAdapter foldersAdapter;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    ArrayList<ImageModel> imageModelsList = new ArrayList<>();
    ArrayList<FoldersModel> foldersModelArrayList = new ArrayList<>();
    Toolbar toolbar;

    //for guillotine menu
    FrameLayout root;
    View contentHamburger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);
        toolbar = findViewById(R.id.mainActivityToolBar);
        recyclerView = findViewById(R.id.foldersRecyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foldersAdapter = new FoldersAdapter(this, foldersModelArrayList);
        recyclerView.setAdapter(foldersAdapter);

        setSupportActionBar(toolbar);

        //main steps
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_menu, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(100)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        recyclerView.addOnItemTouchListener(new ImageClickedListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                Intent intent = new Intent(getApplicationContext(), SingleFolderActivity.class);
                intent.putParcelableArrayListExtra("data", foldersModelArrayList.get(position).getImageModelsList());
                intent.putExtra("bucket", foldersModelArrayList.get(position).getFoldersName());
                startActivity(intent);
            }
        }));

        if ((ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS_CODE);
            }
        } else {
            loadImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    loadImages();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void loadImages() {
        //store bucket name and the image model's associated with it
        HashMap<String, ArrayList<ImageModel>> map = new HashMap<>();
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATA
        };

        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN;

        Cursor cur = managedQuery(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                orderBy + " DESC"       // Ordering
        );
//        Log.i("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int columnIndex = cur.getColumnIndex(MediaStore.Images.Media.DATA);
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                String dateTime = "Unknown";

                try {
                    String format = "dd-MM-yyyy HH:mm";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                    dateTime = dateFormat.format(new Date(Long.parseLong(date)));
                } catch (Exception e) {

                }

                String uriObtained = cur.getString(columnIndex);

                bucket = bucket.substring(0, 1).toUpperCase() + bucket.substring(1).toLowerCase();
                File file = new File(uriObtained);
                if (file.exists()) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setTitle(bucket);
                    imageModel.setUrl(cur.getString(columnIndex));
                    imageModel.setDate(dateTime);
                    imageModelsList.add(imageModel);

                    if (!map.containsKey(bucket)) {
                        ArrayList<ImageModel> temp = new ArrayList<>();
                        temp.add(imageModel);
                        map.put(bucket, temp);
                    } else {
                        ArrayList<ImageModel> current = map.get(bucket);
                        current.add(imageModel);
                        map.remove(bucket);
                        map.put(bucket, current);
                    }
                }

            } while (cur.moveToNext());

            Set<Map.Entry<String, ArrayList<ImageModel>>> st = map.entrySet();

            HashMap<String, ArrayList<ImageModel>> newMap = new HashMap<>();
            Log.d("MAPSIZE", String.valueOf(map.size()));
            for (Map.Entry<String, ArrayList<ImageModel>> me : st) {
                if (me.getValue().size() < 5) {
                    if (newMap.containsKey("Others")) {

                        ArrayList<ImageModel> temp = newMap.get("Others");

                        for (int i = 0; i < me.getValue().size(); i++) {
                            temp.add(me.getValue().get(i));
                        }

                        newMap.put("Others", temp);
                    } else {
                        newMap.put("Others", me.getValue());
                    }
                } else {

                    newMap.put(me.getKey(), me.getValue());
                }
            }

            Set<Map.Entry<String, ArrayList<ImageModel>>> newSet = newMap.entrySet();

            for (Map.Entry<String, ArrayList<ImageModel>> me : newSet) {
                FoldersModel foldersModel = new FoldersModel();
                foldersModel.setFoldersName(me.getKey());
                foldersModel.setImageModelsList(me.getValue());
                foldersModelArrayList.add(foldersModel);
            }
        }
    }
}

