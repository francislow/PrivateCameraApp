package com.example.franc.testcamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    ViewPager viewPager;
    private File currentImageFile;
    private Boolean pictureTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set initial layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set viewpager
        viewPager = (ViewPager)findViewById(R.id.myvp);
        SwipeAdaptor swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdaptor);
        viewPager.setCurrentItem(1);
    }

    //Signal to take a picture
    protected void takePicture() {
        dispatchTakePictureIntent();
    }
    protected File getPicture() {
        return currentImageFile;
    }
    protected boolean wasPictureTaken() {
        return pictureTaken;
    }


    //----------------------CAMERA FUNCTIONS---------------------------------------------------

    //Run camera app to take photo
    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //When camera function fails to starts
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            System.out.println("Problem loading camera");
        }
        //When camera function starts
        else if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            System.out.println("Camera loaded");
            try {
                currentImageFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Error occurred while creating the image =.=");
            }
            //Continue only if the File was successfully created
            if (currentImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        currentImageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //after photo is taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If picture was taken
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            this.pictureTaken = true;
        }
    }

    //Creates an image file with unique names for it
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        return imageFile;
    }
}
