package com.example.franc.unmix.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.example.franc.unmix.ActivityMain;

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
    private Activity currentActivity;

    private String year;
    private String month;
    private String day;


    public MyCamera(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public File getPicture() {
        return currentImageFile;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) == null) {
            System.out.println("Problem loading camera");
        }
        else {
            try {
                currentImageFile = createEmptyFile();
            } catch (IOException ex) {
                System.out.println("Error occurred while creating the an empty file for image");
            }
            // Continue only if the empty file was successfully created
            if (currentImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(currentActivity,
                        "com.example.android.fileprovider",
                        currentImageFile);

                // Add extra instructions to intent to store the image output(EXTRA_OUTPUT) into photoURI of the empty file created
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                currentActivity.startActivityForResult(takePictureIntent, ActivityMain.TAKE_PHOTO_REQUEST);
            }
        }
    }

    //Creates an blank image file with unique names for it
    public File createEmptyFile() throws IOException {
        //To be stored in database
        year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = currentActivity.getExternalFilesDir(ActivityMain.APPIMAGEFOLDERNAME);

        //This will may not give a unique name
        //File imageFile = new File(storageDir, imageFileName + ".jpg");

        //But this will give a unique file name by adding -(some number) to end of file name
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
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
