package com.example.franc.testcamera.Fragments;

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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.ActivityNewNote;
import com.example.franc.testcamera.MyCamera;
import com.example.franc.testcamera.SQLiteDatabases.NotesDatabaseHelper;
import com.example.franc.testcamera.R;
import com.example.franc.testcamera.StickyNoteWidget;

import java.util.ArrayList;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage1 extends Fragment {
    private RelativeLayout rlayout;
    private Button addNoteButton;
    private NotesDatabaseHelper mydb;
    private ArrayList<StickyNoteWidget> stickyNoteWidgetsList;

    //Runs first
    //Mainly to setup variables
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up database
        mydb = new NotesDatabaseHelper(getActivity());

        //Set up sticky notes list
        stickyNoteWidgetsList = new ArrayList<>();
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

        //Relative Layout
        rlayout = (RelativeLayout) getActivity().findViewById(R.id.rlayout);

        //Add note button
        addNoteButton = (Button) getActivity().findViewById(R.id.addnotebutton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityNewNote.class);
                startActivity(intent);
            }
        });

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

        //GET DATA FROM DATABASE: properties of each note
        Cursor res = mydb.getAllData();
        //If there are sticky notes which user saved previously
        if (res.getCount() != 0) {
            //Cycle through each row (each row represents a stickynote)
            while (res.moveToNext()) {
                StickyNoteWidget snw = new StickyNoteWidget(getActivity(), rlayout, res);
                snw.addViewGroup();
                snw.setListener();

                //Create a reference to each stick note to save properties into database when activity pauses
                stickyNoteWidgetsList.add(snw);
            }
        }
    }

    @Override
    public void onPause() {
        Cursor res = mydb.getAllData();

        //UPDATE DATABASE: position, size from sticky notes' text view itself
        //If there are sticky notes that user created
        if (res.getCount() != 0) {
            for (StickyNoteWidget SNW : stickyNoteWidgetsList) {
                if (res.moveToNext()) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) SNW.getViewGroup().getLayoutParams();
                    //position
                    boolean hasUpdatedPositionData = mydb.updatePositionData(res.getInt(0), lp.leftMargin, lp.rightMargin, lp.topMargin, lp.bottomMargin);
                    //size
                    boolean hasUpdatedSizeData = mydb.updateSizeData(res.getInt(0), lp.width, lp.height);

                    if (hasUpdatedPositionData) {
                        Toast.makeText(getActivity(), "Updated postion", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error updating position", Toast.LENGTH_SHORT).show();
                    }

                    if (hasUpdatedSizeData) {
                        Toast.makeText(getActivity(), "Updated size", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Error updating size", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        System.out.println("fragmentpage1 PAUSED");
        super.onPause();
    }
}
