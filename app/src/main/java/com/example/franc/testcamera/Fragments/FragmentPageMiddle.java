package com.example.franc.testcamera.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.PicturesDatabaseHelper;
import com.example.franc.testcamera.R;

import java.io.File;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMiddle extends Fragment {
    private LinearLayout LLOfPictures;

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

        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        Cursor res = mydb.getAllData();
        if (res.getCount() != 0) {
            //Cycle through each row (each row represents a stickynote)
            while (res.moveToNext()) {

                ImageView newImageView = new ImageView(this.getActivity());
                newImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                LLOfPictures.addView(newImageView);

                newImageView.setImageURI(Uri.fromFile(new File(res.getString(1))));
            }
        }
    }


}
