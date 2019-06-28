package com.example.franc.testcamera.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.testcamera.R;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMiddle extends Fragment {
    private LinearLayout LLOfPictures;
    private LinearLayout LLOfThumbnails;
    private ScrollView vScrollView;
    private int screenWidth;
    private int screenHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_middle, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get screen width and height
        screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;

        //LinearLayout
        LLOfPictures = (LinearLayout) getActivity().findViewById(R.id.LL1);
        LLOfThumbnails = (LinearLayout) getActivity().findViewById(R.id.LL2);
        vScrollView = (ScrollView) getActivity().findViewById(R.id.vScrollView);

        //Setup Top Tab
        //Camera Button
        final Button camButton = (Button) getActivity().findViewById(R.id.cambutton);
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
                        if (ActivityMain.touchUpInButton(motionEvent, camButton)) {
                            ActivityMain.myCamera.dispatchTakePictureIntent();
                        }
                        break;
                }
                return true;
            }
        });

        //Photo Button
        final Button addButton = (Button) getActivity().findViewById(R.id.addButton);
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
                        if (ActivityMain.touchUpInButton(motionEvent, addButton)) {
                            //Bring up add photos page
                            Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            getActivity().startActivityForResult(goToGallery, ActivityMain.PICK_IMAGE_REQUEST);
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        System.out.println("fragmentpagemiddle PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        System.out.println("fragmentpagemiddle RESUMED");
        super.onResume();

        // Remove all existing pictures
        LLOfPictures.removeAllViews();
        LLOfThumbnails.removeAllViews();

        // Get database and cursor
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        final Cursor res = mydb.getAllData();


        //While there is data in pictures database
        while (res.moveToNext()) {
            //Get current photo path
            final String currentPhotoPath = res.getString(1);

            //Render recently added pictures
            final ImageView newImageView = new ImageView(this.getActivity());
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(screenWidth, screenHeight / 2);
            lp1.setMargins(0, 24, 0, 0);
            newImageView.setLayoutParams(lp1);
            newImageView.setPadding(20,0,20,0);
            LLOfPictures.addView(newImageView, 0);
            Glide
                    .with(this.getActivity())
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(25))
                    .into(newImageView);

            //Set on click listener
            newImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Version 1 of dialog
                    final Dialog nagDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    nagDialog.setContentView(R.layout.preview_image_page);

                    //Version 2 of dialog
                    //final Dialog nagDialog = new Dialog(getActivity());
                    //nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    //nagDialog.setContentView(R.layout.preview_image_page);

                    ImageView previewImage = (PhotoView) nagDialog.findViewById(R.id.preview_image);
                    Glide
                            .with(nagDialog.getContext())
                            .load(currentPhotoPath)
                            .into(previewImage);

                    nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            nagDialog.dismiss();
                        }
                    });

                    nagDialog.show();
                }
            });


            //Render thumbnails
            ImageView newThumbnail = new ImageView(this.getActivity());
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(screenHeight / 13, screenHeight / 13);
            lp2.setMargins(23, 10, 0, 10);
            newThumbnail.setLayoutParams(lp2);
            LLOfThumbnails.addView(newThumbnail, 0);
            Glide
                    .with(getActivity())
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(60))
                    .into(newThumbnail);

            //Set on click listener
            newThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vScrollView.smoothScrollTo(0, newImageView.getTop());
                }
            });
        }
    }
}
