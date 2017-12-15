package me.kungfucat.gall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.eftimoff.viewpagertransformers.AccordionTransformer;
import com.eftimoff.viewpagertransformers.BackgroundToForegroundTransformer;
import com.eftimoff.viewpagertransformers.CubeOutTransformer;
import com.eftimoff.viewpagertransformers.DefaultTransformer;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.eftimoff.viewpagertransformers.ForegroundToBackgroundTransformer;
import com.eftimoff.viewpagertransformers.RotateDownTransformer;
import com.eftimoff.viewpagertransformers.RotateUpTransformer;
import com.eftimoff.viewpagertransformers.TabletTransformer;
import com.eftimoff.viewpagertransformers.ZoomInTransformer;
import com.eftimoff.viewpagertransformers.ZoomOutSlideTransformer;

import java.util.ArrayList;
import java.util.Random;

public class ShowDetailsActivity extends AppCompatActivity {
    ViewPager viewPager;
//    Toolbar toolbar;
    ArrayList<ImageModel> imageModelArrayList = null;
    public static int currentPosition;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);


        imageModelArrayList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);

//        toolbar=findViewById(R.id.toolBarshowDetails);
//        toolbar.setTitle(imageModelArrayList.get(currentPosition).getTitle());
//        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.darkColour));
//        toolbar.setBackgroundColor(Color.BLACK);
//        toolbar.setElevation(0.5f);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyAdapter(this,getSupportFragmentManager(), imageModelArrayList));
        setRandomPagerAdapter();

        viewPager.setCurrentItem(currentPosition);
        setTitle(imageModelArrayList.get(currentPosition).title);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
//                toolbar.setTitle(imageModelArrayList.get(currentPosition).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setRandomPagerAdapter() {
        Random random = new Random();

        int i = random.nextInt(11);
        switch (i) {
            case 0:
                viewPager.setPageTransformer(true, new AccordionTransformer());
                return;
            case 1:
                viewPager.setPageTransformer(true, new BackgroundToForegroundTransformer());
                return;
            case 2:
                viewPager.setPageTransformer(true, new CubeOutTransformer());
                return;
            case 3:
                viewPager.setPageTransformer(true, new DefaultTransformer());
                return;
            case 4:
                viewPager.setPageTransformer(true, new DepthPageTransformer());
                return;
            case 5:
                viewPager.setPageTransformer(true, new ForegroundToBackgroundTransformer());
                return;
            case 6:
                viewPager.setPageTransformer(true, new RotateDownTransformer());
                return;
            case 7:
                viewPager.setPageTransformer(true, new RotateUpTransformer());
                return;
            case 8:
                viewPager.setPageTransformer(true, new TabletTransformer());
                return;
            case 9:
                viewPager.setPageTransformer(true, new ZoomInTransformer());
                return;
            case 10:
                viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
                return;
        }
    }

    public static class ImageFragment extends Fragment implements View.OnLongClickListener {
        public static ArrayList<ImageModel> models=new ArrayList<>();

        public static ImageFragment newInstance(int position, String title, String url) {
            ImageFragment fragment = new ImageFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putString("title", title);
            args.putString("url", url);
            fragment.setArguments(args);

            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_detail, container, false);
            ImageView imageView = view.findViewById(R.id.imageDetails);

            imageView.setOnLongClickListener(this);

            Bundle bundle = getArguments();
            if (bundle != null) {
                GlideApp.with(getActivity())
                        .load(bundle.getString("url"))
                        .thumbnail(0.5f)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .into(imageView);
            }
            return view;
        }

        //for grey scaled images
        @Override
        public boolean onLongClick(View view) {
            int position=ShowDetailsActivity.currentPosition;
//            Log.d("TAG", String.valueOf(position));
            ImageView imageView= (ImageView) view;
            return false;
        }
    }

    class MyAdapter extends FragmentPagerAdapter {
        public ArrayList<ImageModel> imageModelArrayList = null;
        Context context;

        public MyAdapter(Context context,FragmentManager fm, ArrayList<ImageModel> im) {
            super(fm);
            imageModelArrayList = im;
            this.context=context;
            ImageFragment.models=im;
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment fragment = ImageFragment.newInstance(position,
                    imageModelArrayList.get(position).getTitle(),
                    imageModelArrayList.get(position).getUrl());
            return fragment;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return imageModelArrayList.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return imageModelArrayList.size();
        }
    }
}
