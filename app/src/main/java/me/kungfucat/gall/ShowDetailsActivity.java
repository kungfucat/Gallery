package me.kungfucat.gall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.chrisbanes.photoview.PhotoView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class ShowDetailsActivity extends AppCompatActivity {
    public static ViewPager viewPager;
    public static ArrayList<ImageModel> imageModelArrayList = null;
    public static int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_show_details);

        imageModelArrayList = getIntent().getParcelableArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("position", 0);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MyAdapter(this, getSupportFragmentManager(), imageModelArrayList));
        setRandomPagerTransformer();

        viewPager.setCurrentItem(currentPosition);
        setTitle(imageModelArrayList.get(currentPosition).title);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
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

    public void setRandomPagerTransformer(){
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

    public static class ImageFragment extends Fragment {
        public static ArrayList<ImageModel> models = new ArrayList<>();

        public static ImageFragment newInstance(int position, String title, String url, String date) {
            ImageFragment fragment = new ImageFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putString("title", title);
            args.putString("url", url);
            args.putString("date", date);
            fragment.setArguments(args);

            return fragment;
        }


        @Override
        public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_image_detail, container, false);
            PhotoView photoView = view.findViewById(R.id.imageDetails);

            Bundle bundle = getArguments();
            final int position = bundle.getInt("position");
            final String title = bundle.getString("title");
            final String uriOfImage = bundle.getString("url");
            String dateTaken = bundle.getString("date");


            final Toolbar imageToolbar = view.findViewById(R.id.imageToolbar);
            final Toolbar bottomNavigationBar = view.findViewById(R.id.bottomNavigationBar);

            ImageView deleteImageView = view.findViewById(R.id.deleteIcon);
            ImageView shareImageView = view.findViewById(R.id.shareIcon);
            ImageView backArrowBottomImageView = view.findViewById(R.id.backArrowBottomBar);
            final ImageView moreImageView = view.findViewById(R.id.moreIcon);
            ImageView descriptionImageView = view.findViewById(R.id.descriptionIcon);

            //remove 0 indexing for the user
            int positionToShow = position + 1;
            String bucket = bundle.getString("title");
            TextView imageToolbarTitle = view.findViewById(R.id.imageToolbarTitle);
            ImageView imageToolbarBackArow = view.findViewById(R.id.imageToolbarBackArrow);

            final Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

            backArrowBottomImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    getActivity().onBackPressed();
                }
            });

            imageToolbarBackArow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    getActivity().onBackPressed();
                }
            });

            String stringToShow = bucket + " " + positionToShow + "/" + imageModelArrayList.size();
            imageToolbarTitle.setText(stringToShow);

            final boolean[] hidden = {true};

            final LinearLayout rootOfDetails = view.findViewById(R.id.rootOfDetails);
            final boolean[] areDetailsShown = {false};

            final LinearLayout rootOfMore = view.findViewById(R.id.rootOfMore);
            final boolean[] moreLayoutIsVisible = {false};

            TextView textView1 = view.findViewById(R.id.textView1);
            TextView textView2 = view.findViewById(R.id.textView2);
            TextView textView3 = view.findViewById(R.id.textView3);
            TextView textView4 = view.findViewById(R.id.textView4);

            File file = new File(uriOfImage);
            final Uri uri = Uri.parse(uriOfImage);
            String textView1String = "Name : " + uri.getLastPathSegment();
            String textView2String = "Path : " + uri;

            textView1.setText(textView1String);
            textView2.setText(textView2String);

            String dateToShow = "Date & Time : " + dateTaken;
            textView3.setText(dateToShow);

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

            textView4.setText(textView4String);


            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (hidden[0]) {
                        bottomNavigationBar.setVisibility(View.VISIBLE);
                        imageToolbar.setVisibility(View.VISIBLE);

                        bottomNavigationBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        imageToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                        hidden[0] = false;
                    } else {
                        //no need to hide it immediately
//                        imageToolbar.setVisibility(View.GONE);
                        rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        bottomNavigationBar.animate().translationY(bottomNavigationBar.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        imageToolbar.animate().translationY(-imageToolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                        rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();

                        //GONE to escape confusion in landscape mode
                        rootOfDetails.setVisibility(View.GONE);
                        rootOfMore.setVisibility(View.GONE);
                        moreLayoutIsVisible[0] = false;
                        areDetailsShown[0] = false;
                        hidden[0] = true;
                    }
                }
            });

            shareImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);

                    rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();

                    //GONE to escape confusion in landscape mode
                    rootOfDetails.setVisibility(View.GONE);
                    rootOfMore.setVisibility(View.GONE);
                    moreLayoutIsVisible[0] = false;
                    areDetailsShown[0] = false;

                    Uri uri = Uri.parse(uriOfImage);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_TEXT, "");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, "Share"));
                }
            });


            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);

                    rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                    rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();

                    //GONE to escape confusion in landscape mode
                    rootOfDetails.setVisibility(View.GONE);
                    rootOfMore.setVisibility(View.GONE);
                    moreLayoutIsVisible[0] = false;
                    areDetailsShown[0] = false;

                    final int pos = position + 1;
                    final File imageToDelete = new File(uriOfImage);
                    if (imageToDelete.exists()) {
                        new MaterialStyledDialog
                                .Builder(getActivity())
                                .setTitle("Delete")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderColor(R.color.black)
                                .setDescription("Are you sure you want to delete this image?")
                                .withDarkerOverlay(true)
                                .setCancelable(true)
                                .setPositiveText("Yes")
                                .withDivider(true)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        if (imageToDelete.delete()) {
                                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(uriOfImage))));
                                            ShowDetailsActivity.imageModelArrayList.remove(pos - 1);
                                            Intent intent = new Intent(getActivity(), SingleFolderActivity.class);
                                            intent.putExtra("bucket", title);
                                            intent.putExtra("data", ShowDetailsActivity.imageModelArrayList);
                                            startActivity(intent);
                                            Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();

                                        } else {
                                            getActivity().finish();
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


            descriptionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    if (areDetailsShown[0] == false) {

                        if (moreLayoutIsVisible[0] == true) {
                            rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                            moreLayoutIsVisible[0] = false;
                            rootOfMore.setVisibility(View.GONE);
                        }
                        rootOfDetails.setVisibility(View.VISIBLE);
                        rootOfDetails.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        areDetailsShown[0] = true;
                    } else {
                        rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        areDetailsShown[0] = false;
                        rootOfDetails.setVisibility(View.GONE);
                    }
                }
            });

            moreImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    if (moreLayoutIsVisible[0] == false) {
                        if (areDetailsShown[0] == true) {
                            rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                            areDetailsShown[0] = false;
                            rootOfDetails.setVisibility(View.GONE);
                        }
                        rootOfMore.setVisibility(View.VISIBLE);
                        rootOfMore.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        moreLayoutIsVisible[0] = true;
                    } else {
                        rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        moreLayoutIsVisible[0] = false;
                        rootOfMore.setVisibility(View.GONE);
                    }
                }
            });

//MORE OPTIONS ON CLICK LISTENERS
            TextView detailsOfImage = view.findViewById(R.id.detailsOfImageInMore);
            TextView setAsImage = view.findViewById(R.id.setAsImage);
            TextView cropImage = view.findViewById(R.id.cropImage);
            TextView editImage = view.findViewById(R.id.editImage);

            detailsOfImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    if (areDetailsShown[0] == false) {

                        if (moreLayoutIsVisible[0] == true) {
                            rootOfMore.animate().translationY(rootOfMore.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                            moreLayoutIsVisible[0] = false;
                            rootOfMore.setVisibility(View.GONE);
                        }
                        rootOfDetails.setVisibility(View.VISIBLE);
                        rootOfDetails.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                        areDetailsShown[0] = true;
                    } else {
                        rootOfDetails.animate().translationY(rootOfDetails.getTop()).setInterpolator(new AccelerateInterpolator()).start();
                        areDetailsShown[0] = false;
                        rootOfDetails.setVisibility(View.GONE);
                    }
                }
            });

            setAsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    //can't use normal URI, because original uri misses the 'file://' prefix
                    intent.setDataAndType(Uri.fromFile(new File(uriOfImage)), "image/*");
                    intent.putExtra("mimeType", "image/*");
                    getActivity().startActivity(Intent.createChooser(intent, "Set Image"));
                }
            });

            cropImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        vibrator.vibrate(50);
                        CropImage.activity(Uri.fromFile(new File(uriOfImage)))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(getActivity());

                    }
                    // respond to users whose devices do not support the crop action
                    catch (Exception e) {
                        // display an error message
                        Log.d("UNSUCCESS", String.valueOf(e));
                    }
                }
            });

            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibrator.vibrate(50);
                    Intent editIntent = new Intent(Intent.ACTION_EDIT);
                    editIntent.setDataAndType(Uri.fromFile(new File(uriOfImage)), "image/*");
                    editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        getActivity().startActivity(editIntent);
                    } catch (Exception e) {
                        try {
                            getActivity().startActivity(Intent.createChooser(editIntent, "Edit"));
                        } catch (Exception e2) {
                            Toast.makeText(getActivity(), "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            GlideApp.with(getActivity())
                    .load(uriOfImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(0.5f)
                    .placeholder(new ColorDrawable(Color.BLACK))
                    .into(photoView);

            return view;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath + "/Cropped Images/");
                dir.mkdir();

                // Create a name for the saved image
                String nameOfImage = "Gall_Cropped" + System.currentTimeMillis() + ".jpg";
                File destinationFileName = new File(dir, nameOfImage);
                String sourceFileName = result.getUri().getPath();
                BufferedInputStream bis = null;
                BufferedOutputStream bos = null;
                try {
                    bis = new BufferedInputStream(new FileInputStream(sourceFileName));
                    bos = new BufferedOutputStream(new FileOutputStream(destinationFileName, false));
                    byte[] buf = new byte[1024];
                    bis.read(buf);
                    do {
                        bos.write(buf);
                    } while (bis.read(buf) != -1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bis != null) bis.close();
                        if (bos != null) bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destinationFileName)));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    class MyAdapter extends FragmentPagerAdapter {
        public ArrayList<ImageModel> imageModelArrayList = null;
        Context context;

        public MyAdapter(Context context, FragmentManager fm, ArrayList<ImageModel> im) {
            super(fm);
            imageModelArrayList = im;
            this.context = context;
            ImageFragment.models = im;
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment fragment = ImageFragment.newInstance(position,
                    imageModelArrayList.get(position).getTitle(),
                    imageModelArrayList.get(position).getUrl(),
                    imageModelArrayList.get(position).getDate());
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
