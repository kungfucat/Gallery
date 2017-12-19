package me.kungfucat.gall;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter;
import me.kungfucat.gall.interfaces.OnItemClickListener;

public class SingleFolderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<ImageModel> imageModelsList = null;
    Toolbar toolbar;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_single_folder);
        toolbar = findViewById(R.id.toolBarForSingleFolder);
        recyclerView = findViewById(R.id.recyclerViewForAllImages);
        context = this;


        imageModelsList = getIntent().getParcelableArrayListExtra("data");
        String title = getIntent().getStringExtra("bucket");
        if (title == null || title.equals("")) {
            title = getResources().getString(R.string.app_name);
        }
        TextView toolbarTextView = findViewById(R.id.singleFolderToolbarTitle);
        toolbarTextView.setText(title);

        final Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        ImageView toolBarBackArrow = findViewById(R.id.singleFoldertoolbarBackArrow);
        toolBarBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.vibrate(50);
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });


        if (imageModelsList.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        adapter = new MyAdapter(this, imageModelsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        SlideInLeftAnimationAdapter animationAdapter = new SlideInLeftAnimationAdapter(adapter);
        recyclerView.setAdapter(animationAdapter);

        recyclerView.addOnItemTouchListener(new ImageClickedListener(this, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), ShowDetailsActivity.class);
                intent.putParcelableArrayListExtra("data", imageModelsList);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        }));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}

