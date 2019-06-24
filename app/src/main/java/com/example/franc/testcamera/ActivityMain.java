package com.example.franc.testcamera;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    private MyCamera myCamera;
    private static final int PICK_IMAGE = 2;

    public static int lastViewedFragItem = 1;

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

        //Setup Top Tab
        //Camera Button
        final Button camButton = (Button) findViewById(R.id.cambutton);
        camButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        camButton.setAlpha(0.1f);
                        camButton.setScaleX(0.5f);
                        camButton.setScaleY(0.5f);
                        break;

                    case MotionEvent.ACTION_UP:
                        camButton.setAlpha(1f);
                        camButton.setScaleX(1f);
                        camButton.setScaleY(1f);

                        //If user's touch up is still inside button
                        if (touchUpInButton(motionEvent, camButton)) {
                            myCamera.dispatchTakePictureIntent();
                        }
                        break;
                }
                return true;
            }
        });

        //Photo Button
        final Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addButton.setAlpha(0.1f);
                        addButton.setScaleX(0.5f);
                        addButton.setScaleY(0.5f);
                        break;

                    case MotionEvent.ACTION_UP:
                        addButton.setAlpha(1f);
                        addButton.setScaleX(1f);
                        addButton.setScaleY(1f);

                        //If user's touch up is still inside button
                        if (touchUpInButton(motionEvent, addButton)) {
                            //Bring up add photos page
                            Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(goToGallery, PICK_IMAGE);
                        }
                        break;
                }
                return true;
            }
        });
    }

    //Handles activity results in all fragments
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //After user took the photo
        if (requestCode == MyCamera.TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            //*Note: In this case data will be null since in camera,
            // I have added instructions to put data into photoURI instead

            //Store picture into database
            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(this);

            boolean hasInsertedData = mydb.insertData(myCamera.getPicture().getAbsolutePath(),
                    null, myCamera.getYear(), myCamera.getMonth(), myCamera.getDay());

            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
        }

        //After user picked an image from gallery
        else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri galleryImageUri = data.getData();

            //Make a copy of the image and store into app folder
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "JPEG_" + timeStamp;
            Uri newUri = copyMediaStoreUriToCacheDir(galleryImageUri, fileName);

            //Store picture into database
            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(this);
            boolean hasInsertedData = mydb.insertData(newUri.getPath(),
                    null, null, null, null);

            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Uri copyMediaStoreUriToCacheDir(Uri uri, String filename) {
        String destinationFilename = this.getExternalFilesDir("CameraPictures") + "/" + filename + ".jpg";
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
    public boolean touchUpInButton(MotionEvent motionEvent, Button button) {
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
