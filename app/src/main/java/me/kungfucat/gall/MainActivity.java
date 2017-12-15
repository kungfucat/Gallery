package me.kungfucat.gall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.kungfucat.gall.interfaces.OnItemClickListener;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FoldersAdapter foldersAdapter;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    ArrayList<ImageModel> imageModelsList = new ArrayList<>();
    ArrayList<FoldersModel> foldersModelArrayList = new ArrayList<>();
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.mainActivityToolBar);
        recyclerView = findViewById(R.id.foldersRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        foldersAdapter = new FoldersAdapter(this, foldersModelArrayList);
        recyclerView.setAdapter(foldersAdapter);


        String title = getResources().getString(R.string.app_name);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.darkColour));
        toolbar.setBackgroundColor(Color.BLACK);
        setSupportActionBar(toolbar);

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
            int columnIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);

                bucket = bucket.substring(0, 1).toUpperCase() + bucket.substring(1).toLowerCase();
                ImageModel imageModel = new ImageModel();
                imageModel.setTitle(bucket);
                imageModel.setUrl(cur.getString(columnIndex));
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
            } while (cur.moveToNext());

            Set<Map.Entry<String, ArrayList<ImageModel>>> st = map.entrySet();

            HashMap<String, ArrayList<ImageModel>> newMap = new HashMap<>();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d("TAG","CLICKED");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

