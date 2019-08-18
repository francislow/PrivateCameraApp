package com.example.franc.unmix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyCamera;
import com.example.franc.unmix.Utilities.MyUtilities;
import com.example.franc.unmix.Utilities.SwipeAdaptor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    public static MyCamera MYCAMERA;
    public PicturesDatabaseHelper mydb;
    public static SwipeAdaptor swipeAdaptor;
    public static final int TAKE_PHOTO_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final String DEFAULTCATEGORYNAME = "Unsorted";
    public static final String APPIMAGEFOLDERNAME = "UserPictures";

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set initial layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set up camera
        MYCAMERA = new MyCamera(this);

        //Set up database
        mydb = new PicturesDatabaseHelper(this);

        //Set viewpager
        swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.myvp);
        viewPager.setAdapter(swipeAdaptor);
        viewPager.setCurrentItem(0);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    MyUtilities.createOneTimeIntroDialog(ActivityMain.this,"first_time_page2", R.drawable.starting_dialog2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //Setup Btm Tab
        TabLayout btmTabLayout = (TabLayout) findViewById(R.id.btmtablayout);
        btmTabLayout.setupWithViewPager(viewPager);
        /*btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_pin);*/
        btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        btmTabLayout.getTabAt(1).setIcon(R.drawable.ic_gallery);
        btmTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    //Handles activity results in all fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        //After user took the photo
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            //*Note: In this case data will be null since in camera,
            // I have added instructions to put data into photoURI instead

            //Store picture into database
            boolean hasInsertedData = mydb.insertNewRowPTable(MYCAMERA.getPicture().getAbsolutePath(),
                    DEFAULTCATEGORYNAME, "", null, year, month, day, hour, min, seconds);


            if (hasInsertedData) {
                Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error adding", Toast.LENGTH_LONG).show();
            }

            // intro
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        }

        //After user picked an image from gallery
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            System.out.println("IT RANNNNNN pcik image");
            Uri galleryImageUri = data.getData();

            //Make a copy of the image and store into app folder
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "JPEG_" + timeStamp;
            Uri newUri = MyUtilities.copyMediaStoreUriToCacheDir(galleryImageUri, fileName, this);

            //Store picture into database
            boolean hasInsertedData = mydb.insertNewRowPTable(newUri.getPath(),
                    DEFAULTCATEGORYNAME, "", null, year, month, day, hour, min, seconds);


            if (hasInsertedData) {
                Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error adding", Toast.LENGTH_LONG).show();
            }

            // intro
            MyUtilities.createOneTimeIntroDialog(this,"first_time_page1_2", R.drawable.starting_dialog1_2);
        }
    }
}
