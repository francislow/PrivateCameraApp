package com.example.franc.testcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityMain extends FragmentActivity {
    File currentImageFile;
    ImageView imageView;
    Button button;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set initial layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove phone's notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //-------------------View Pager-----------------------------------

        viewPager = (ViewPager)findViewById(R.id.myvp);
        SwipeAdaptor swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        viewPager.setAdapter(swipeAdaptor);

        //----------------------------------------------------------------


        //Image of photo taken
        imageView = (ImageView) findViewById(R.id.hello1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("I CLICKED YOUR IMAGE DUDEE");
            }
        });

        //Camera button
        button = (Button) findViewById(R.id.hello);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        //Change page button
        button = (Button) findViewById(R.id.hello3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityMain.this, Activity1.class);
                startActivity(intent);
            }
        });
    }

























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
            // Continue only if the File was successfully created
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
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //showing picture as an image view in xml design
            String currentPhotoPath = currentImageFile.getAbsolutePath();
            File imgFile = new  File(currentPhotoPath);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
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
