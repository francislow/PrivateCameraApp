package com.chalkboystudios.franc.unmix.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Handles camera intents
 */
public class MyCamera {
    private static final String TAG = "MyCamera";
    private File currentImageFile;
    private Activity currentActivity;

    public MyCamera(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    /**
     * Dispatch intent to take photo and stores image into a image file
     */
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) == null) {
            Log.e(TAG, "dispatchTakePictureIntent: Problem loading camera");
        } else {
            try {
                currentImageFile = MyUtilities.createEmptyFile(currentActivity);
            } catch (IOException ex) {
                Log.e(TAG, "dispatchTakePictureIntent: " +
                        "Error occurred while creating the an empty file for image");
            }
            // Continue only if the empty file was successfully created
            if (currentImageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(currentActivity,
                        "com.example.android.fileprovider",
                        currentImageFile);

                // Image taken will be written to photoURI path
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                currentActivity.startActivityForResult(takePictureIntent, RequestCodeHelper.TAKE_PHOTO_REQUEST);
            }
        }
    }

    /**
     * Returns the image file that contains the image taken by user
     *
     * @return String of the most recent image file path
     */
    public String getPictureFilePath() {
        return currentImageFile.getAbsolutePath();
    }
}
