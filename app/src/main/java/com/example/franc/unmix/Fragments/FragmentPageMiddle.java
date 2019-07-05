package com.example.franc.unmix.Fragments;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.franc.unmix.ActivityMain;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.R;
import com.example.franc.unmix.Utilities.MyUtilities;
import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMiddle extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private LinearLayout LLOfPictures;
    private LinearLayout LLOfThumbnails;
    private ScrollView vScrollView;
    private int screenWidth;
    private int screenHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_middle, container, false);
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
        camButton.setOnTouchListener(this);

        //Photo Button
        final Button addButton = (Button) getActivity().findViewById(R.id.addButton);
        addButton.setOnTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Remove all existing pictures
        LLOfPictures.removeAllViews();
        LLOfThumbnails.removeAllViews();

        // Get database and cursor
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        final Cursor res = mydb.getAllDataPTable();

        //While there is data in pictures database
        while (res.moveToNext()) {
            //Get current photo path
            String currentPhotoPath = res.getString(1);

            // Set up image view
            ImageView newImageView = new ImageView(this.getActivity());
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(screenWidth, screenHeight / 2);
            lp1.setMargins(0, 24, 0, 0);
            newImageView.setLayoutParams(lp1);
            newImageView.setPadding(20, 0, 20, 0);
            LLOfPictures.addView(newImageView, 0);
            //Put image into image view
            Glide
                    .with(this.getActivity())
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(25))
                    .into(newImageView);
            //Set on click listener for the image view
            newImageView.setTag(currentPhotoPath);
            newImageView.setOnClickListener(this);

            //Set up thumbnail (image view)
            ImageView newThumbnail = new ImageView(this.getActivity());
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(screenHeight / 13, screenHeight / 13);
            lp2.setMargins(23, 10, 0, 10);
            newThumbnail.setLayoutParams(lp2);
            LLOfThumbnails.addView(newThumbnail, 0);
            // Put image into thumbnail (image view)
            Glide
                    .with(getActivity())
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(60))
                    .into(newThumbnail);
            //Set on click listener
            newThumbnail.setTag(newImageView);
            newThumbnail.setOnClickListener(this);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setAlpha(0.1f);
                view.setScaleX(0.5f);
                view.setScaleY(0.5f);
                break;

            case MotionEvent.ACTION_UP:
                view.setAlpha(1f);
                view.setScaleX(1f);
                view.setScaleY(1f);

                // If user's touch up is still in the button
                if (MyUtilities.touchUpInButton(motionEvent, (Button) view)) {
                    switch (view.getId()) {
                        // If it is a camera button
                        case R.id.cambutton:
                            // Start camera
                            ActivityMain.MYCAMERA.dispatchTakePictureIntent();
                            break;

                        // If it is an add from gallery button
                        case R.id.addButton:
                            // Start gallery
                            Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            getActivity().startActivityForResult(goToGallery, ActivityMain.PICK_IMAGE_REQUEST);
                            break;
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (((LinearLayout) view.getParent()).getOrientation()) {
            // If it is the display pictures
            case LinearLayout.VERTICAL:
                String currentPhotoPath = (String) view.getTag();
                final Dialog nagDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                nagDialog.setContentView(R.layout.dialog_preview_image);

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
                break;

            // If it is the thumbnails
            case LinearLayout.HORIZONTAL:
                ImageView correspondingIV = (ImageView)view.getTag();
                vScrollView.smoothScrollTo(0, correspondingIV.getTop());
                break;
        }
    }
}
