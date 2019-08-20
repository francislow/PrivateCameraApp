package com.chalkboystudios.franc.unmix.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.chalkboystudios.franc.unmix.ActivityMain;

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

    public MyCamera(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) == null) {
            System.out.println("Problem loading camera");
        }
        else {
            try {
                currentImageFile = MyUtilities.createEmptyFile(currentActivity);
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

    public File getPictureFile() {
        return currentImageFile;
    }
}
