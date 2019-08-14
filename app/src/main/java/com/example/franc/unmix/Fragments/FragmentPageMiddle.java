package com.example.franc.unmix.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.franc.unmix.ActivityMain;
import com.example.franc.unmix.CustomInformationDialog;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.R;
import com.example.franc.unmix.Utilities.MyUtilities;
import com.github.chrisbanes.photoview.PhotoView;

import static android.content.ContentValues.TAG;

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

        MyUtilities.createOneTimeIntroDialog(getActivity(),"first_time_page1", R.drawable.starting_dialog1);

        // Create initial app intro dialog
        String prefKey = "intro_app_pref_key";
        Log.d(TAG, "createOneTimeIntroDialog: tried running one time dialog");
        SharedPreferences prefs = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE);
        boolean first_time_flag = prefs.getBoolean(prefKey, true);//"No name defined" is the default value.

        if (first_time_flag) {
            // if no entry add first time flag  = false as entry
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(prefKey, false);
            editor.apply();

            // if first time starting app, apply dialog
            final Dialog myDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            // Set Layout
            myDialog.setContentView(R.layout.dialog_intro_app);
            ImageView informationIV = myDialog.findViewById(R.id.informationIV);

            // Set dialog background to transparent
            myDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

            // Set appear and disappear transitions
            myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFade;

            // Set unable to use back button to cancel
            myDialog.setCancelable(false);
            
            Button cancelDialogButton = (Button) myDialog.findViewById(R.id.cancel_dialog_button);
            cancelDialogButton.setVisibility(View.VISIBLE);
            cancelDialogButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
                    PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
                    PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f);

                    PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.5f);
                    PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.5f);
                    PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.5f);
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaDown, scaleXDown, scaleYDown).start();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();
                            break;
                        case MotionEvent.ACTION_UP:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();

                            //If user's touch up is still inside button
                            if (MyUtilities.touchUpInButton(motionEvent, (Button) view)) {
                                myDialog.dismiss();
                            }
                    }
                    return true;
                }
            });
            myDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Remove all pictures and thumbnails
        LLOfPictures.removeAllViews();
        LLOfThumbnails.removeAllViews();

        // Render all pictures and thumbnails
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        final Cursor res = mydb.getAllDataPTable();
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
        PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
        PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
        PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f);

        PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.5f);
        PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.5f);
        PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.5f);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator.ofPropertyValuesHolder(view, alphaDown, scaleXDown, scaleYDown).start();
                break;
            case MotionEvent.ACTION_CANCEL:
                ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();
                break;
            case MotionEvent.ACTION_UP:
                ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();

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
                TextView tv = (TextView) getActivity().findViewById(R.id.recentlyadded);
                ImageView correspondingIV = (ImageView)view.getTag();
                vScrollView.smoothScrollTo(0, correspondingIV.getTop() + tv.getHeight());
                break;
        }
    }
}
