package com.chalkboystudios.franc.unmix;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.chalkboystudios.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.chalkboystudios.franc.unmix.Utilities.MyCamera;
import com.chalkboystudios.franc.unmix.Utilities.MyUtilities;
import com.chalkboystudios.franc.unmix.Utilities.SwipeAdaptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String DEFAULT_CAT_NAME = "Unsorted";
    public static final String IMAGE_FOLDER_NAME = "UserPictures";

    // Request codes
    public static final int TAKE_PHOTO_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;

    private static MyCamera myCamera;
    private PicturesDatabaseHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set initial layout
        setContentView(R.layout.activity_main);

        myCamera = new MyCamera(this);
        mydb = new PicturesDatabaseHelper(this);

        // Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set viewpager
        SwipeAdaptor swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.myvp);
        viewPager.setAdapter(swipeAdaptor);
        viewPager.setCurrentItem(0);

        // Setup first time tutorial guide
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyUtilities.createOneTimeIntroDialog(ActivityMain.this,
                                    "first_time_page2", R.drawable.starting_dialog2);
                        }
                    }, 400);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setup Btm Tab
        TabLayout btmTabLayout = (TabLayout) findViewById(R.id.btmtablayout);
        btmTabLayout.setupWithViewPager(viewPager);
        btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        btmTabLayout.getTabAt(1).setIcon(R.drawable.ic_gallery);
        btmTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    // Handles activity results in all fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);

        // After user took a photo
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            //*Note: In this case, data will be null since in camera,
            // I have added instructions to put data into photoURI instead

            // Store picture into database
            boolean hasInsertedData = mydb.insertNewRowPTable(myCamera.getPictureFile().getAbsolutePath(),
                    DEFAULT_CAT_NAME, "", null, year, month, day, hour, min, seconds);

            if (hasInsertedData) {
                Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error adding", Toast.LENGTH_LONG).show();
            }

            // Setup first time tutorial guide
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        }
        // After user picked an image from gallery
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            //Make a copy of the image and store into app folder
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "JPEG_" + timeStamp;
            Uri userImageUri = data.getData();
            Uri newUri = MyUtilities.copyMediaStoreUriToCacheDir(userImageUri, fileName, this);

            //Store picture into database
            boolean hasInsertedData = mydb.insertNewRowPTable(newUri.getPath(),
                    DEFAULT_CAT_NAME, "", null, year, month, day, hour, min, seconds);


            if (hasInsertedData) {
                Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error adding", Toast.LENGTH_LONG).show();
            }

            // Setup first time tutorial guide
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        }
    }

    public static MyCamera getCamera() {
        return myCamera;
    }
}
