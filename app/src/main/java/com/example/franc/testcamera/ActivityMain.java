package com.example.franc.testcamera;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    public static MyCamera myCamera;

    public static final int TAKE_PHOTO_REQUEST = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final String DEFAULTCATEGORYNAME = "Unsorted";
    public static final String IMAGEFOLDERNAME = "UserPictures";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set initial layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set up camera
        myCamera = new MyCamera(this);

        //Set viewpager
        SwipeAdaptor swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.myvp);
        viewPager.setAdapter(swipeAdaptor);
        viewPager.setCurrentItem(1);

        //Setup Btm Tab
        TabLayout btmTabLayout = (TabLayout) findViewById(R.id.btmtablayout);
        btmTabLayout.setupWithViewPager(viewPager);
        btmTabLayout.getTabAt(0).setIcon(R.drawable.ic_notes);
        btmTabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
        btmTabLayout.getTabAt(2).setIcon(R.drawable.ic_gallery);

    }

    //Handles activity results in all fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //After user took the photo
        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            //*Note: In this case data will be null since in camera,
            // I have added instructions to put data into photoURI instead

            //Store picture into database
            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(this);

            boolean hasInsertedData = mydb.insertNewRowPTable(myCamera.getPicture().getAbsolutePath(),
                    DEFAULTCATEGORYNAME, myCamera.getYear(), myCamera.getMonth(), myCamera.getDay());


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
            Uri newUri = copyMediaStoreUriToCacheDir(galleryImageUri, fileName);

            //Store picture into database
            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(this);
            boolean hasInsertedData = mydb.insertNewRowPTable(newUri.getPath(),
                    DEFAULTCATEGORYNAME, null, null, null);


            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Uri copyMediaStoreUriToCacheDir(Uri uri, String filename) {
        String destinationFilename = this.getExternalFilesDir(IMAGEFOLDERNAME) + "/" + filename + ".jpg";
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(getContentResolver().openInputStream(uri));
//          bis = new BufferedInputStream(App.getAppContext().getContentResolver().openInputStream(uri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);

            return Uri.fromFile(new File(destinationFilename));
        } catch (IOException e) {
            //
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                System.out.println("copyMediaStoreUriToCacheDir ran into IOException shucks..");
            }
        }
        return null;
    }

    //If user touched down and up a button within button space
    public static boolean touchUpInButton(MotionEvent motionEvent, Button button) {
        int[] buttonPosition = new int[2];
        button.getLocationOnScreen(buttonPosition);

        if (motionEvent.getRawX() >= buttonPosition[0] && motionEvent.getRawX() <= (buttonPosition[0] + button.getWidth())) {
            if (motionEvent.getRawY() >= buttonPosition[1] && motionEvent.getRawY() <= (buttonPosition[1] + button.getHeight())) {
                return true;
            }
        }
        return false;
    }
}
