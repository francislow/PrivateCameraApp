package com.example.franc.testcamera.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.testcamera.R;
import com.example.franc.testcamera.Utilities.ImageUtilities;

import java.io.File;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMiddle extends Fragment {
    private LinearLayout LLOfPictures;
    private LinearLayout LLOfThumbnails;
    private ScrollView vScrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page_middle, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //LinearLayout
        LLOfPictures = (LinearLayout) getActivity().findViewById(R.id.LL1);
        LLOfThumbnails = (LinearLayout) getActivity().findViewById(R.id.LL2);
        vScrollView = (ScrollView) getActivity().findViewById(R.id.vScrollView);
    }

    @Override
    public void onPause() {
        System.out.println("fragmentpagemiddle PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        ActivityMain.lastViewedFragItem = 1;
        System.out.println("fragmentpagemiddle RESUMED");
        super.onResume();

        //Remove all existing pictures
        LLOfPictures.removeAllViews();
        LLOfThumbnails.removeAllViews();

        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        Cursor res = mydb.getAllData();
        if (res.getCount() != 0) {
            //Cycle through each row (each row represents a stickynote)
            while (res.moveToNext()) {
                //Render images
                final ImageView newImageView = new ImageView(this.getActivity());
                newImageView.setAdjustViewBounds(true);
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp1.setMargins(0,0,0,50);
                newImageView.setLayoutParams(lp1);
                LLOfPictures.addView(newImageView, 0);

                //Resize bitmap before displaying to prevent out of memory error
                final Bitmap newBitMap = ImageUtilities.shrinkBitmap(res.getString(1), 1000, 1000);
                newImageView.setImageBitmap(newBitMap);

                //on click
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

                        ImageView previewImage = (ImageView) nagDialog.findViewById(R.id.preview_image);
                        previewImage.setImageBitmap(newBitMap);

                        /*
                        #find out how to use this#
                        nagDialog.setCanceledOnTouchOutside();
                        */
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
                newThumbnail.setAdjustViewBounds(true);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp2.setMargins(23,10,0,10);
                newThumbnail.setLayoutParams(lp2);
                LLOfThumbnails.addView(newThumbnail, 0);

                //Resize bitmap to make it a square picture
                Bitmap resized = Bitmap.createScaledBitmap(newBitMap, 100, 100, true);

                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(null, resized);
                roundedBitmapDrawable.setCornerRadius(12.5f);
                newThumbnail.setImageDrawable(roundedBitmapDrawable);

                //SetOnclicklistener to jump to image when thumbnail is clicked
                newThumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vScrollView.smoothScrollTo(0, newImageView.getTop());
                    }
                });
            }
        }
    }
}
