package me.kungfucat.gall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import me.kungfucat.gall.interfaces.OnItemClickListener;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    ArrayList<ImageModel> imageModelsList = new ArrayList<>();
    public static String TEMP_IMG = "https://picsum.photos/400/600/?image=";

//    public static String IMGS[] = {
//            "https://images.unsplash.com/photo-1444090542259-0af8fa96557e?q=80&fm=jpg&w=1080&fit=max&s=4b703b77b42e067f949d14581f35019b",
//            "https://picsum.photos/600/900/?random",
//            "https://picsum.photos/600/900/?random",
//            "https://picsum.photos/600/900/?random",
//            "https://picsum.photos/600/900/?random",
//            "https://picsum.photos/600/900/?random",
//            "https://picsum.photos/600/900/?random",
//            "https://media.giphy.com/media/l2Sq5GffrCyUMEXjW/giphy.gif",
//            "https://images.unsplash.com/photo-1444090542259-0af8fa96557e?q=80&fm=jpg&w=1080&fit=max&s=4b703b77b42e067f949d14581f35019b",
//            "https://images.unsplash.com/photo-1439546743462-802cabef8e97?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1437651025703-2858c944e3eb?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://media.giphy.com/media/26mkhIj7fJHjq0JMI/giphy.gif",
//            "https://images.unsplash.com/photo-1431538510849-b719825bf08b?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1434873740857-1bc5653afda8?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1439396087961-98bc12c21176?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1433616174899-f847df236857?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1438480478735-3234e63615bb?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300",
//            "https://images.unsplash.com/photo-1438027316524-6078d503224b?dpr=2&fit=crop&fm=jpg&h=725&q=50&w=1300"
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        settingAdapter();
        recyclerView.addOnItemTouchListener(new ImageClickedListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("POSITION", position + "");
                Intent intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
                intent.putParcelableArrayListExtra("data", imageModelsList);
                intent.putExtra("position", position);
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

    public void settingAdapter() {
        String IMGS[] = new String[1000];

        int start = 0, end = 10;
        for (int i = start; i < end; i++) {
            IMGS[i - start] = TEMP_IMG + i;
        }
        for (int i = 0; i < end - start; i++) {
            ImageModel model = new ImageModel();
            model.setTitle("Image at " + i);
            model.setUrl(IMGS[i]);
            imageModelsList.add(model);
        }
        myAdapter = new MyAdapter(this, imageModelsList);
        recyclerView.setAdapter(myAdapter);
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

    private void loadImages() {
    }
}

