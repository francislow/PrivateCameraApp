package com.example.franc.testcamera;

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

import com.example.franc.testcamera.UserinputHandlers.NoteInputListener;

import java.util.ArrayList;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage1 extends Fragment {
    private RelativeLayout rlayout;
    private Button addNoteButton;
    private DatabaseHelper mydb;
    private ArrayList<StickyNoteWidget> stickyNoteWidgetsList;


    //Runs first
    //Mainly to setup variables
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up database
        mydb = new DatabaseHelper(getActivity());

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
                Intent intent = new Intent(getActivity(), NewNoteActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityMain.lastViewedFragItem = 0;


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
