package com.example.franc.unmix.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.franc.unmix.R;

import java.util.ArrayList;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage1 extends Fragment {
    private RelativeLayout rlayout;
    private Button addNoteButton;

    //Runs first
    //Mainly to setup variables
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up database
    }

    //Mainly to setup layout
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page1, container, false);
        return view;
    }

    //Called immediately after onCreateView(..) once
    //Mainly to setup views
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
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
        */
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
