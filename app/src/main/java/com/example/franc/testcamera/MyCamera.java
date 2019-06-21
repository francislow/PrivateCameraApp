package com.example.franc.testcamera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by franc on 16/6/2019.
 */

public class MyCamera {
    private File currentImageFile;
    private Boolean pictureTaken = false;
    private Activity currentActivity;

    String year;
    String month;
    String day;

    public MyCamera(Activity currentActivity) {
        this.currentActivity = currentActivity;
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
    protected void setPictureTaken(boolean bool) {
        this.pictureTaken = bool;
    }

    //----------------------CAMERA FUNCTIONS---------------------------------------------------

    //Run camera app to take photo
    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //When camera function fails to starts
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) == null) {
            System.out.println("Problem loading camera");
        }
        //When camera function starts
        else if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) != null) {
            System.out.println("Camera loaded");
            try {
                currentImageFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                System.out.println("Error occurred while creating the image =.=");
            }
            //Continue only if the empty file was successfully created
            if (currentImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(currentActivity,
                        "com.example.android.fileprovider",
                        currentImageFile);

                //Put image into empty file created
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                currentActivity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //Creates an blank image file with unique names for it
    private File createImageFile() throws IOException {
        //To be stored in database
        year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = currentActivity.getExternalFilesDir("CameraPictures");
        //This will may not give a unique name
        //File imageFile = new File(storageDir, imageFileName + ".jpg");
        //But this will give a unique file name by adding -(some number) to end of file name
        File imageFile = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",         //suffix
                storageDir      //directory
        );
        // Save a file: path for use with ACTION_VIEW intents
        return imageFile;


        //save file name to database getAbsolutePath()|year|month|day|label
    }

    public String getYear(){
        return this.year;
    }

    public String getMonth() {
        return this.month;
    }

    public String getDay() {
        return this.day;
    }
}
