package com.example.franc.testcamera;

import android.content.Intent;
import android.content.SharedPreferences;
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


import java.util.Calendar;

import static com.example.franc.testcamera.MyCamera.REQUEST_TAKE_PHOTO;

public class ActivityMain extends FragmentActivity {
    private ViewPager viewPager;
    private MyCamera myCamera;
    private SwipeAdaptor swipeAdaptor;
    public static int lastViewedFragItem = 1;

    private static final int PICK_IMAGE = 100;

    private static final String SAVED_FILE_NAME = "MySavedFiles";

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
        swipeAdaptor = new SwipeAdaptor(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.myvp);
        viewPager.setAdapter(swipeAdaptor);
        viewPager.setCurrentItem(lastViewedFragItem);

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
                        if(touchUpInsideButton(motionEvent, camButton))
                        myCamera.takePicture();

                        break;
                }
                return true;
            }
        });

        //Add Photo Button
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

                        Intent gallery;
                        //If user's touch up is still inside button
                        System.out.println("checkpoint2");
                        if(touchUpInsideButton(motionEvent, addButton)) {
                            //Bring up add photos page
                            System.out.println("checkpoint");
                            gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            startActivityForResult(gallery, PICK_IMAGE);
                        }

                        break;
                }
                return true;
            }
        });
    }
    //after photo is taken
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If picture was taken, set view
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            myCamera.setPictureTaken(true);

            //Store picture into database
            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(this);
            boolean hasInsertedData = mydb.insertData(myCamera.getPicture().getAbsolutePath(),
                    null, myCamera.getYear(), myCamera.getMonth(), myCamera.getDay());
            if (hasInsertedData) {
                Toast.makeText(this, "Picture successfully inserted into database", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Error inserting picture into database", Toast.LENGTH_LONG).show();
            }
        }

        //If
        else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri tbImageUri = data.getData();

            SharedPreferences settings0 = getSharedPreferences(SAVED_FILE_NAME, 0);
            SharedPreferences.Editor mySettings0Edit = settings0.edit();
            mySettings0Edit.putString("tbImageUri", tbImageUri.toString());
            mySettings0Edit.commit();
        }
    }

    public MyCamera getMyCamera() {
        return myCamera;
    }

    public boolean touchUpInsideButton(MotionEvent motionEvent, Button button) {
        int[] buttonPosition = new int[2];
        button.getLocationOnScreen(buttonPosition);

        //Check
        if(motionEvent.getRawX() >= buttonPosition[0] && motionEvent.getRawX() <= (buttonPosition[0]+button.getWidth())) {
            if(motionEvent.getRawY() >= buttonPosition[1] && motionEvent.getRawY() <= (buttonPosition[1]+button.getHeight())) {
                return true;
            }
        }
        return false;
    }


    public Uri getTbImageUri() {
        SharedPreferences settings0 = getSharedPreferences(SAVED_FILE_NAME, 0);
        String tbImageUriString = settings0.getString("tbImageUri", null);
        return Uri.parse(tbImageUriString);
    }
}
