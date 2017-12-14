package me.kungfucat.gall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import me.kungfucat.gall.interfaces.OnItemClickListener;

public class SingleFolderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter adapter;
    ArrayList<ImageModel> imageModelsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_folder);
        imageModelsList = getIntent().getParcelableArrayListExtra("data");
        recyclerView=findViewById(R.id.recyclerViewForAllImages);

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

