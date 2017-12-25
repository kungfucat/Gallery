package me.kungfucat.gall;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.eftimoff.viewpagertransformers.DefaultTransformer;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import github.chenupt.springindicator.SpringIndicator;
import me.kungfucat.gall.fragments.ImageFoldersFragment;

public class MainActivity extends AppCompatActivity {
    //TODO: Remove the meta-data from manifest and re-enable the crash analytics
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    ArrayList<FoldersModel> foldersModelArrayList;
    ArrayList<FoldersModel> foldersModelArrayListVideos;
    Toolbar toolbar;
    ViewPager mainViewPager;
    SpringIndicator indicator;
    MainPagerAdapter mainPagerAdapter;
    ArrayList<Bitmap> bitmapArrayList;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        foldersModelArrayList = new ArrayList<>();
        foldersModelArrayListVideos = new ArrayList<>();
        bitmapArrayList=new ArrayList<>();


        toolbar = findViewById(R.id.mainActivityToolBar);

        setSupportActionBar(toolbar);
        mainViewPager = findViewById(R.id.mainViewPager);
        indicator = findViewById(R.id.indicator);

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
            loadVideos();
        }

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), foldersModelArrayList);
        mainViewPager.setAdapter(mainPagerAdapter);
        indicator.setViewPager(mainViewPager);
        mainViewPager.setPageTransformer(true, new DefaultTransformer());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    loadImages();
                    loadVideos();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void loadVideos() {
        //store bucket name and the image model's associated with it

        HashMap<String, ArrayList<ImageModel>> map = new HashMap<>();

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.DATA
        };

        Uri images = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN;

        Cursor cur = managedQuery(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                orderBy + " DESC"       // Ordering
        );
        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int columnIndex = cur.getColumnIndex(MediaStore.Video.Media.DATA);
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            int dateColumn = cur.getColumnIndex(
                    MediaStore.Video.Media.DATE_TAKEN);

            do {
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);
                String dateTime = "Unknown";

                try {
                    String format = "dd-MM-yyyy HH:mm";
                    SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
                    dateTime = dateFormat.format(new Date(Long.parseLong(date)));
                } catch (Exception e) {
                    dateTime = "Unknown";
                }

                String uriObtained = cur.getString(columnIndex);

                bucket = bucket.substring(0, 1).toUpperCase() + bucket.substring(1).toLowerCase();
                File file = new File(uriObtained);
                String mimeType = URLConnection.guessContentTypeFromName(cur.getString(columnIndex));

                if (file.exists() && mimeType != null && mimeType.startsWith("video")) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setTitle(bucket);
                    imageModel.setUrl(cur.getString(columnIndex));
                    imageModel.setDate(dateTime);
                    imageModel.setAVideo(true);

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

            for (Map.Entry<String, ArrayList<ImageModel>> me : st) {
                FoldersModel foldersModel = new FoldersModel();
                foldersModel.setFoldersName(me.getKey());
                foldersModel.setImageModelsList(me.getValue());
                for (int i = 0; i < me.getValue().size(); i++) {
                    Log.d("VIDEOURIS", me.getValue().get(i).getUrl());
                }
                foldersModelArrayListVideos.add(foldersModel);
            }

        }
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
                    dateTime = "Unknown";
                }

                String uriObtained = cur.getString(columnIndex);

                bucket = bucket.substring(0, 1).toUpperCase() + bucket.substring(1).toLowerCase();
                File file = new File(uriObtained);
                String mimeType = URLConnection.guessContentTypeFromName(cur.getString(columnIndex));
                if (file.exists() && mimeType != null && mimeType.startsWith("image")) {
                    ImageModel imageModel = new ImageModel();
                    imageModel.setTitle(bucket);
                    imageModel.setUrl(cur.getString(columnIndex));
                    imageModel.setDate(dateTime);

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

            for (Map.Entry<String, ArrayList<ImageModel>> me : st) {
                FoldersModel foldersModel = new FoldersModel();
                foldersModel.setFoldersName(me.getKey());
                foldersModel.setImageModelsList(me.getValue());
                foldersModelArrayList.add(foldersModel);
            }

        }

    }

    class MainPagerAdapter extends FragmentPagerAdapter {
        String[] tabs = {"Images", "Videos"};
        ArrayList<FoldersModel> foldersModelArrayList;

        public MainPagerAdapter(FragmentManager fm, ArrayList<FoldersModel> list) {
            super(fm);
            foldersModelArrayList = list;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment myFragment = null;
            if (position == 0) {
                ImageFoldersFragment fragment = ImageFoldersFragment.newInstance(position, foldersModelArrayList);
                myFragment = fragment;
            }
            if (position == 1) {
                ImageFoldersFragment fragment = ImageFoldersFragment.newInstance(position, foldersModelArrayListVideos);
                myFragment = fragment;
            }
            return myFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }
}

