package com.example.franc.unmix.Fragments;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.unmix.ActivityMain;
import com.example.franc.unmix.CustomPicture;
import com.example.franc.unmix.R;
import com.example.franc.unmix.RecyclerViewAdaptor;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyUtilities;

import java.util.ArrayList;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment implements View.OnTouchListener, View.OnDragListener {
    private PicturesDatabaseHelper mydb;
    private Button addCatButton;
    private TextView textView;
    private Button switchLabelViewButton;

    public static boolean labelViewFlag = false;

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

        // Setup Top Tab
        // App name text view
        textView = (TextView) getActivity().findViewById(R.id.appname2);
        // Dustbin image view
        final ImageView dustbin = (ImageView) getActivity().findViewById(R.id.dustbin);
        dustbin.getBackground().setAlpha(0);
        dustbin.setOnDragListener(this);
        // Switch to label view button
        switchLabelViewButton = (Button) getActivity().findViewById(R.id.switch_to_label);
        switchLabelViewButton.setOnTouchListener(this);
        // Photo Button
        addCatButton = (Button) getActivity().findViewById(R.id.add_cat_button);
        addCatButton.setOnTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up distinctCategoryNames list
        ArrayList<String> distinctCategoryNames = setUpDistinctCategoryNamesList();

        // Set up photoPath list
        ArrayList<ArrayList<String>> photoPathLists = setUpPhotoPathList(distinctCategoryNames);

        // Initialise recycler view
        initRecyclerView(distinctCategoryNames, photoPathLists);
    }

    // Set up distinctCategoryNames list
    public ArrayList<String> setUpDistinctCategoryNamesList() {
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

        return distinctCategoryNames;
    }

    // Set up photo path list
    public ArrayList<ArrayList<String>> setUpPhotoPathList(ArrayList<String> distinctCategoryNames) {
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
        return photoPathLists;
    }

    public void initRecyclerView(ArrayList<String> distinctCategoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        RecyclerViewAdaptor recyclerViewAdaptor = new RecyclerViewAdaptor(this, distinctCategoryNames, photoPathLists);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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

                //If user's touch up is still inside button
                if (MyUtilities.touchUpInButton(motionEvent, (Button) view)) {
                    switch (view.getId()) {
                        case R.id.add_cat_button:
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
                                String newCategoryName = categoryNameText.getText().toString().trim();
                                if (!MyUtilities.hasDuplicatedCatNamesInCTable(newCategoryName, getActivity())) {
                                    boolean hasInsertedData = mydb.insertNewRowCTable(newCategoryName);
                                    if (hasInsertedData) {
                                        onResume();
                                        Toast.makeText(getActivity(), "successfully added to database", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error adding to database", Toast.LENGTH_LONG).show();
                                    }
                                    nagDialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Unable to add label, you already have an exact label", Toast.LENGTH_LONG).show();
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
                        break;
                        case R.id.switch_to_label:
                            if (labelViewFlag) {
                                labelViewFlag = false;
                            }
                            else {
                                labelViewFlag = true;
                            }
                            onResume();
                            break;
                    }
                }
        }
        return true;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        // view -> the dustbin
        CustomPicture draggedImage = (CustomPicture) event.getLocalState();
        GridLayout oldGridView = (GridLayout) draggedImage.getParent();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                addCatButton.getBackground().setAlpha(0);
                textView.setAlpha(0);
                view.getBackground().setAlpha(255);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                addCatButton.getBackground().setAlpha(255);
                textView.setAlpha(1);
                view.getBackground().setAlpha(0);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                view.setScaleX(1.5f);
                view.setScaleY(1.5f);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                view.setScaleX(1f);
                view.setScaleY(1f);
                break;
            case DragEvent.ACTION_DROP:
                view.setScaleX(1f);
                view.setScaleY(1f);
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
}
