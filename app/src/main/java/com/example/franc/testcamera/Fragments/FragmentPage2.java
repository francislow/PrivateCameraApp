package com.example.franc.testcamera.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.R;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.testcamera.Utilities.ImageUtilities;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment {
    GridLayout gridLayout;
    private LinearLayout LLScreen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridLayout = (GridLayout) getActivity().findViewById(R.id.grid1);
        LLScreen = (LinearLayout) getActivity().findViewById(R.id.LL3);


    }

    @Override
    public void onPause() {
        System.out.println("fragmentpage2 PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        ActivityMain.lastViewedFragItem = 2;
        System.out.println("fragmentpage2 RESUMED");
        super.onResume();

        gridLayout.removeAllViews();

        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        Cursor res = mydb.getAllData();
        if (res.getCount() != 0) {
            //Cycle through each row (each row represents a stickynote)
            while (res.moveToNext()) {
                //Render Label Text view
                if (res.getString(2) != null) {
                    //LLScreen.addView();
                }
                else {

                }

                //Render images
                final ImageView newImageView = new ImageView(this.getActivity());
                newImageView.setAdjustViewBounds(true);
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(gridWidth / 3, gridWidth / 3);
                lp1.setMargins(100, 100, 100, 100);
                newImageView.setLayoutParams(lp1);
                gridLayout.addView(newImageView, 0);

                //Resize bitmap before displaying to prevent out of memory error
                final Bitmap newBitMap = ImageUtilities.shrinkBitmap(res.getString(1), 1000, 1000);
                Bitmap resized = Bitmap.createScaledBitmap(newBitMap, gridWidth / 3, gridWidth / 3, true);
                newImageView.setImageBitmap(resized);

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

            }
        }
    }
}
