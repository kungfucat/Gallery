package me.kungfucat.gall;

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
import android.transition.Explode;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.SlideInRightAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
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
        toolbar = findViewById(R.id.toolBarForSingleFolder);
        recyclerView = findViewById(R.id.recyclerViewForAllImages);


        imageModelsList = getIntent().getParcelableArrayListExtra("data");
        String title = getIntent().getStringExtra("bucket");
        if (title == null || title.equals("")) {
            title = getResources().getString(R.string.app_name);
        }
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.darkColour));
        toolbar.setBackgroundColor(Color.BLACK);
        toolbar.setElevation(0.5f);

        adapter = new MyAdapter(this, imageModelsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

//        recyclerView.setAdapter(adapter);
//        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapter);
//        scaleInAnimationAdapter.setDuration(600);
//        recyclerView.setAdapter(new SlideInLeftAnimationAdapter(scaleInAnimationAdapter));

        recyclerView.setAdapter(new SlideInLeftAnimationAdapter(adapter));



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

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}

