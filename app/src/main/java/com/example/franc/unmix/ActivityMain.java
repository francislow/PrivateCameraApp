package com.example.franc.unmix;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyCamera;
import com.example.franc.unmix.Utilities.MyUtilities;
import com.example.franc.unmix.Utilities.SwipeAdaptor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    public static MyCamera MYCAMERA;
    public PicturesDatabaseHelper mydb;
    public static SwipeAdaptor swipeAdaptor;
    public static final int TAKE_PHOTO_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final String DEFAULTCATEGORYNAME = "Unsorted";
    public static final String APPIMAGEFOLDERNAME = "UserPictures";

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
        viewPager.setCurrentItem(1);

        //Setup Btm Tab
        TabLayout btmTabLayout = (TabLayout) findViewById(R.id.btmtablayout);
        btmTabLayout.setupWithViewPager(viewPager);
        btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_pin);
        btmTabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
        btmTabLayout.getTabAt(2).setIcon(R.drawable.ic_gallery);
        btmTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

    }

    //Handles activity results in all fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //After user took the photo
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            //*Note: In this case data will be null since in camera,
            // I have added instructions to put data into photoURI instead

            //Store picture into database
            boolean hasInsertedData = mydb.insertNewRowPTable(MYCAMERA.getPicture().getAbsolutePath(),
                    DEFAULTCATEGORYNAME, "", null, null);


            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
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
                    DEFAULTCATEGORYNAME, "", null, null);


            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
        }
    }


}
