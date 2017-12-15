package me.kungfucat.gall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import me.kungfucat.gall.interfaces.OnItemClickListener;

public class SingleFolderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<ImageModel> imageModelsList = null;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_folder);
        toolbar=findViewById(R.id.toolBarForSingleFolder);
        recyclerView=findViewById(R.id.recyclerViewForAllImages);

        imageModelsList = getIntent().getParcelableArrayListExtra("data");
        String title=getIntent().getStringExtra("bucket");
        if(title==null || title==""){
            title=getResources().getString(R.string.app_name);
        }
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.darkColour));
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setElevation(0.5f);

        adapter=new MyAdapter(this,imageModelsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new ImageClickedListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                Log.d("POSITION", position + "");
                Intent intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
                intent.putParcelableArrayListExtra("data", imageModelsList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }));
    }
}

