package me.kungfucat.gall;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class ShowDetailsVideoActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    public static ArrayList<ImageModel> imageModelArrayList = null;
    public static int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details_video);

        imageModelArrayList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);
    }
}
