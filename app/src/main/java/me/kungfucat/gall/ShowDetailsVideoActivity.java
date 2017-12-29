package me.kungfucat.gall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class ShowDetailsVideoActivity extends AppCompatActivity {

    public static ViewPager viewPager;
    public static ArrayList<ImageModel> imageModelArrayList = null;
    public static int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_show_details_video);

        imageModelArrayList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);
        viewPager = findViewById(R.id.videoViewPager);

        viewPager.setAdapter(new VideoPagerAdapter(this, getSupportFragmentManager(), imageModelArrayList));
        viewPager.setCurrentItem(currentPosition);
        setRandomPagerTransformer();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void setRandomPagerTransformer() {
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

    public static class VideoFragment extends Fragment {
        public static ArrayList<ImageModel> models = new ArrayList<>();

        public static VideoFragment newInstance(int position, String title, String url, String date) {
            VideoFragment fragment = new VideoFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putString("title", title);
            args.putString("url", url);
            args.putString("date", date);
            fragment.setArguments(args);

            return fragment;
        }



        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_video_detail, container, false);

            final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            final Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
            final String uriOfVideo = bundle.getString("url");
            final String title = bundle.getString("title");
            String dateTaken = bundle.getString("date");
            final ImageView placeHolderImageView = view.findViewById(R.id.placeHolderImageView);
            final ImageView clickToPlayImageView = view.findViewById(R.id.clickToPlayImageView);
            final VideoView videoView = view.findViewById(R.id.videoView);
            final Uri fileUriOfVideo = Uri.parse(String.valueOf(new File(uriOfVideo)));

            final Toolbar videoToolbar = view.findViewById(R.id.videoToolbar);
            ImageView videoToolbarBackArrow = view.findViewById(R.id.videoToolbarBackArrow);
            TextView videoToolbarTextView = view.findViewById(R.id.videoTitle);
            final boolean[] isUpperToolbarVisible = {false};

            final Toolbar bottomvideoToolbar = view.findViewById(R.id.bottomVideoBar);
            ImageView backvideoBottomBar = view.findViewById(R.id.videoBackBottomBarArrow);
            ImageView shareVideoBottomBar = view.findViewById(R.id.shareIconVideo);
            ImageView deleteVideoBottomBar = view.findViewById(R.id.deleteIconVideo);
            ImageView descriptionVideoBottomBar = view.findViewById(R.id.descriptionIconVideo);
            final boolean[] isBottomToolbarVisible = {false};

            final LinearLayout rootOfVideoDetails = view.findViewById(R.id.rootOfVideoDetails);
            TextView videoTextView1 = view.findViewById(R.id.videoTextView1);
            TextView videoTextView2 = view.findViewById(R.id.videoTextView2);
            TextView videoTextView3 = view.findViewById(R.id.videoTextView3);
            TextView videoTextView4 = view.findViewById(R.id.videoTextView4);
            final boolean[] areVideoDetailsShown = {false};

            int positionToShow = position + 1;
            String textToShow = title + " " + positionToShow + "/" + imageModelArrayList.size();
            videoToolbarTextView.setText(textToShow);

            Uri uri = Uri.parse(uriOfVideo);
            File file = new File(uriOfVideo);

            String textView1String = "Name : " + uri.getLastPathSegment();
            String textView2String = "Path : " + uri;

            videoTextView1.setText(textView1String);
            videoTextView2.setText(textView2String);

            String dateToShow = "Date & Time : " + dateTaken;
            videoTextView3.setText(dateToShow);

            long length = file.length();
            length /= 1024;
            String textView4String;
            long mbLength = length / 1024;
            if (mbLength != 0) {
                length /= 102;
                textView4String = "Size : " + mbLength + "." + length + " MB";
            } else {
                textView4String = "Size : " + length + " KB";
            }

            videoTextView4.setText(textView4String);


            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            clickToPlayImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    ImageView clickImageView = (ImageView) view;

                    isUpperToolbarVisible[0] = false;
                    videoToolbar.animate().translationY(-videoToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    videoToolbar.setVisibility(View.GONE);

                    isBottomToolbarVisible[0] = false;
                    bottomvideoToolbar.animate().translationY(bottomvideoToolbar.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    bottomvideoToolbar.setVisibility(View.GONE);

                    areVideoDetailsShown[0] = false;
                    rootOfVideoDetails.animate().translationY(rootOfVideoDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    rootOfVideoDetails.setVisibility(View.GONE);


                    clickImageView.setVisibility(View.GONE);
                    placeHolderImageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(fileUriOfVideo);
                    videoView.requestFocus();
                    videoView.start();
                }
            });

            placeHolderImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isUpperToolbarVisible[0]) {
                        isUpperToolbarVisible[0] = false;
                        videoToolbar.animate().translationY(-videoToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        videoToolbar.setVisibility(View.GONE);

                        areVideoDetailsShown[0] = false;
                        rootOfVideoDetails.animate().translationY(rootOfVideoDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        rootOfVideoDetails.setVisibility(View.GONE);

                        isBottomToolbarVisible[0] = false;
                        bottomvideoToolbar.animate().translationY(bottomvideoToolbar.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        bottomvideoToolbar.setVisibility(View.GONE);

                    } else {
                        isBottomToolbarVisible[0] = true;
                        bottomvideoToolbar.setVisibility(View.VISIBLE);
                        bottomvideoToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                        isUpperToolbarVisible[0] = true;
                        videoToolbar.setVisibility(View.VISIBLE);
                        videoToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    }
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoView.stopPlayback();
                    videoView.setVisibility(View.GONE);
                    clickToPlayImageView.setVisibility(View.VISIBLE);
                    placeHolderImageView.setVisibility(View.VISIBLE);
                }
            });

            videoToolbarBackArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    getActivity().onBackPressed();

                }
            });

            backvideoBottomBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    getActivity().onBackPressed();
                }
            });

            shareVideoBottomBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);


                    areVideoDetailsShown[0] = false;
                    rootOfVideoDetails.animate().translationY(rootOfVideoDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    rootOfVideoDetails.setVisibility(View.GONE);

                    Uri uri = Uri.parse(uriOfVideo);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("video/*");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            });

            deleteVideoBottomBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);


                    areVideoDetailsShown[0] = false;
                    rootOfVideoDetails.animate().translationY(rootOfVideoDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    rootOfVideoDetails.setVisibility(View.GONE);

                    final int pos = position + 1;
                    final File videoToDelete = new File(uriOfVideo);
                    if (videoToDelete.exists()) {
                        new MaterialStyledDialog
                                .Builder(getActivity())
                                .setTitle("Delete")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderColor(R.color.black)
                                .setDescription("Are you sure you want to delete this video?")
                                .withDarkerOverlay(true)
                                .setCancelable(true)
                                .setPositiveText("Yes")
                                .withDivider(true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        if (videoToDelete.delete()) {

                                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(uriOfVideo))));
                                            ShowDetailsVideoActivity.imageModelArrayList.remove(pos - 1);
                                            Intent intent = new Intent(getActivity(), SingleFolderActivity.class);
                                            intent.putExtra("bucket", title);
                                            intent.putExtra("data", ShowDetailsVideoActivity.imageModelArrayList);
                                            startActivity(intent);
                                            getActivity().finish();
                                            Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeText("No")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    }
                                })
                                .show();

                    } else {
                        Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_LONG).show();
                    }
                }
            });

            rootOfVideoDetails.setOnClickListener(null);
            descriptionVideoBottomBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    if (areVideoDetailsShown[0] == true) {

                        areVideoDetailsShown[0] = false;
                        rootOfVideoDetails.animate().translationY(rootOfVideoDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        rootOfVideoDetails.setVisibility(View.GONE);

                    } else {
                        rootOfVideoDetails.setVisibility(View.VISIBLE);
                        rootOfVideoDetails.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        areVideoDetailsShown[0] = true;
                    }
                }
            });


            GlideApp.with(this)
                    .load(uriOfVideo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(0.5f)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(placeHolderImageView);
            return view;
        }
    }

    class VideoPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<ImageModel> imageModelArrayList;
        Context context;


        public VideoPagerAdapter(Context context, FragmentManager fm, ArrayList<ImageModel> im) {
            super(fm);
            imageModelArrayList = im;
            this.context = context;
            VideoFragment.models = im;
        }


        @Override
        public Fragment getItem(int position) {
            return VideoFragment.newInstance(position,
                    imageModelArrayList.get(position).getTitle(),
                    imageModelArrayList.get(position).getUrl(),
                    imageModelArrayList.get(position).getDate());
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
