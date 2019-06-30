package com.example.franc.testcamera.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.ActivityNewNote;
import com.example.franc.testcamera.R;
import com.example.franc.testcamera.RecyclerViewAdaptor;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.testcamera.Utilities.ImageUtilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment {
    PicturesDatabaseHelper mydb;

    public static ImageView dustbin;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        mydb = new PicturesDatabaseHelper(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Setup Top Tab
        dustbin = (ImageView) getActivity().findViewById(R.id.dustbin);
        dustbin.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                ImageView draggedImage = (ImageView) event.getLocalState();
                GridLayout oldGridView = (GridLayout) draggedImage.getParent();        // v -> parentlayout
                // view -> the dragged picture
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        dustbin.setScaleX(2);
                        dustbin.setScaleY(2);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        dustbin.setScaleX(1f);
                        dustbin.setScaleY(1f);
                        break;
                    case DragEvent.ACTION_DROP:
                        dustbin.setScaleX(1f);
                        dustbin.setScaleY(1f);
                        oldGridView.removeView(draggedImage);
                        boolean hasDeletedData = mydb.deleteData((String) draggedImage.getTag());
                        if (hasDeletedData) {
                            Toast.makeText(getActivity(), "Successfully deleted picture", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Error deleting picture", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //Photo Button
        final Button addCatButton = (Button) getActivity().findViewById(R.id.add_cat_button);
        addCatButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addCatButton.setAlpha(0.1f);
                        addCatButton.setScaleX(0.5f);
                        addCatButton.setScaleY(0.5f);
                        break;

                    case MotionEvent.ACTION_UP:
                        addCatButton.setAlpha(1f);
                        addCatButton.setScaleX(1f);
                        addCatButton.setScaleY(1f);

                        //If user's touch up is still inside button
                        if (ActivityMain.touchUpInButton(motionEvent, addCatButton)) {
                            //Add a category
                            final Dialog nagDialog = new Dialog(getActivity());
                            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            nagDialog.setContentView(R.layout.insert_cat_name_page);

                            //Set add category button on click listener
                            Button submitButton = (Button) nagDialog.findViewById(R.id.button1);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText categoryNameText = (EditText) nagDialog.findViewById(R.id.editT1);
                                    String categoryName = categoryNameText.getText().toString().trim();
                                    boolean hasInsertedData = mydb.insertCategoryNameData(categoryName);
                                    if (hasInsertedData) {
                                        onResume();
                                        Toast.makeText(getActivity(), "successfully added to database", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error adding to database", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    nagDialog.dismiss();
                                }
                            });

                            nagDialog.show();
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up distinctCategoryNames list
        List<String> distinctCategoryNames = new ArrayList<>();
        Cursor res1 = mydb.getCategoryNameData();
        distinctCategoryNames.add(ActivityMain.DEFAULTCATEGORYNAME);
        while (res1.moveToNext()) {
            String currentCategory = res1.getString(1);
            distinctCategoryNames.add(currentCategory);
        }
        // Set up photoPath list
        ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
        Cursor res2 = mydb.getAllData();
        for (int i = 0; i < distinctCategoryNames.size(); i++) {
            res2.moveToFirst();
            res2.moveToPrevious();
            ArrayList<String> photoPaths = new ArrayList<>();
            while (res2.moveToNext()) {
                // If picture belongs to the category
                if (res2.getString(2).equals(distinctCategoryNames.get(i))) {
                    photoPaths.add(res2.getString(1));
                }
            }
            photoPathLists.add(photoPaths);
        }

        initRecyclerView(distinctCategoryNames, photoPathLists);
    }

    public void initRecyclerView(List<String> distinctCategoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        RecyclerViewAdaptor recyclerViewAdaptor = new RecyclerViewAdaptor(this, distinctCategoryNames, photoPathLists);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
