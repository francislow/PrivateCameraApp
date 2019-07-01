package com.example.franc.testcamera.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.R;
import com.example.franc.testcamera.RecyclerViewAdaptor;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;

import java.util.ArrayList;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment {
    PicturesDatabaseHelper mydb;

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
        final ImageView dustbin = (ImageView) getActivity().findViewById(R.id.dustbin);
        dustbin.getBackground().setAlpha(0);
        dustbin.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                ImageView draggedImage = (ImageView) event.getLocalState();
                GridLayout oldGridView = (GridLayout) draggedImage.getParent();        // v -> parentlayout view -> the dragged picture
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        dustbin.getBackground().setAlpha(255);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        dustbin.getBackground().setAlpha(0);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        dustbin.setScaleX(1.5f);
                        dustbin.setScaleY(1.5f);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        dustbin.setScaleX(1f);
                        dustbin.setScaleY(1f);
                        break;
                    case DragEvent.ACTION_DROP:
                        dustbin.setScaleX(1f);
                        dustbin.setScaleY(1f);
                        oldGridView.removeView(draggedImage);
                        boolean hasDeletedData = mydb.deleteRowPTable((String) draggedImage.getTag());
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
                            nagDialog.setContentView(R.layout.dialog_insert_cat_name);

                            //Set add category button on click listener
                            Button submitButton = (Button) nagDialog.findViewById(R.id.button1);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText categoryNameText = (EditText) nagDialog.findViewById(R.id.editT1);
                                    String categoryName = categoryNameText.getText().toString().trim();
                                    boolean hasInsertedData = mydb.insertNewRowCTable(categoryName);
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
        ArrayList<String> distinctCategoryNames = new ArrayList<>();
        Cursor res3 = mydb.getAllDataCTable();

        boolean hasDefaultCatName = false;
        while (res3.moveToNext()) {
            String currentCatName = res3.getString(1);
            if (currentCatName.equals(ActivityMain.DEFAULTCATEGORYNAME)) {
                hasDefaultCatName = true;
            }
        }
        if (!hasDefaultCatName) {
            mydb.insertNewRowCTable(ActivityMain.DEFAULTCATEGORYNAME);
        }

        Cursor res1 = mydb.getAllDataCTable();
        while (res1.moveToNext()) {
            String currentCatName = res1.getString(1);
            distinctCategoryNames.add(currentCatName);
        }
        // Set up photoPath list
        ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
        Cursor res2 = mydb.getAllDataPTable();
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

    public void initRecyclerView(ArrayList<String> distinctCategoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        RecyclerViewAdaptor recyclerViewAdaptor = new RecyclerViewAdaptor(this, distinctCategoryNames, photoPathLists);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
