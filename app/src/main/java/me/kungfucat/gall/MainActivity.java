package me.kungfucat.gall;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;

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
import petrov.kristiyan.colorpicker.ColorPicker;

public class MainActivity extends AppCompatActivity {
    //TODO: Remove the meta-data from manifest and re-enable the crash analytics
    RecyclerView recyclerView;
    FoldersAdapter foldersAdapter;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    ArrayList<FoldersModel> foldersModelArrayList = new ArrayList<>();


    Toolbar toolbar;
    ViewPager mainViewPager;
    SpringIndicator indicator;
    MainPagerAdapter mainPagerAdapter;

    Context context;
    //for guillotine menu
    FrameLayout root;
    View contentHamburger;
    LinearLayout colorPickerLinearLayout;
    HashMap<String, ArrayList<ImageModel>> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        map = new HashMap<>();

        root = findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);
        toolbar = findViewById(R.id.mainActivityToolBar);

        setSupportActionBar(toolbar);
        mainViewPager = findViewById(R.id.mainViewPager);
        indicator = findViewById(R.id.indicator);

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), foldersModelArrayList);
        mainViewPager.setAdapter(mainPagerAdapter);
        indicator.setViewPager(mainViewPager);

        //main steps
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_menu, null);
        root.addView(guillotineMenu);
        colorPickerLinearLayout = guillotineMenu.findViewById(R.id.colorPickerLinearLayout);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(10)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

        LinearLayout rootGuillotine = findViewById(R.id.rootGuillotineLinearLayout);

        //to prevent onClick's from the guillotine menu
        rootGuillotine.setOnClickListener(null);
        final LinearLayout groupSmallFolders = findViewById(R.id.groupSmallFoldersLinearLayout);
        groupSmallFolders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwitchCompat switchCompat = groupSmallFolders.findViewById(R.id.groupSmallFoldersSwitcher);
                switchCompat.toggle();
            }
        });

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

        colorPickerLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPicker colorPicker = new ColorPicker((Activity) context);
                colorPicker.show();
            }
        });
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

    public static class ImageFoldersFragment extends Fragment {

        public ImageFoldersFragment(){

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
            ArrayList<FoldersModel> foldersModel = bundle.getParcelableArrayList("foldersData");
            recyclerView.setAdapter(new FoldersAdapter(getContext(), foldersModel));

            return view;
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
            ImageFoldersFragment fragment = ImageFoldersFragment.newInstance(position, foldersModelArrayList);
            return fragment;
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

