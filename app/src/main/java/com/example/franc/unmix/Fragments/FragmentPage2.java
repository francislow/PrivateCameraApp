package com.example.franc.unmix.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.unmix.ActivityMain;
import com.example.franc.unmix.CustomInformationDialog;
import com.example.franc.unmix.CustomPicture;
import com.example.franc.unmix.R;
import com.example.franc.unmix.RecyclerViewAdaptor;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyUtilities;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment implements View.OnTouchListener, View.OnDragListener {
    private PicturesDatabaseHelper mydb;
    private Button addCatButton;
    private Button informationButton;
    private TextView appNameTV;
    private RelativeLayout topTabSpace;
    private RelativeLayout topTabSpace2;
    private TextView dustbinTV;

    private RecyclerView recyclerView;
    public RecyclerViewAdaptor recyclerViewAdaptor;

    // Constants when drag of a specific picture started
    public static GridLayout oldGridLayout;
    public static CustomPicture draggedPicture;
    public static int oldGridPosition;

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
        topTabSpace = (RelativeLayout) getActivity().findViewById(R.id.top_tab_space);
        topTabSpace2 = (RelativeLayout) getActivity().findViewById(R.id.top_tab_space_2);
        topTabSpace2.setOnDragListener(this);
        topTabSpace2.setAlpha(0);
        // Dustbin imageview
        dustbinTV = (TextView) getActivity().findViewById(R.id.dustbin);
        dustbinTV.setAlpha(0);
        // App name textview
        appNameTV = (TextView) getActivity().findViewById(R.id.appname2);

        // Photo Button
        addCatButton = (Button) getActivity().findViewById(R.id.add_cat_button);
        addCatButton.setOnTouchListener(this);

        informationButton = (Button) getActivity().findViewById(R.id.information_button);
        informationButton.setOnTouchListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        recyclerView.setAdapter(null);
        System.out.println("fragm 2 paused");
    }



    @Override
    public void onResume() {
        super.onResume();
        // Get data from database
        ArrayList<String> distinctCategoryNames = setUpDistinctCategoryNamesList();
        ArrayList<ArrayList<String>> photoPathLists = setUpPhotoPathList(distinctCategoryNames);
        ArrayList<ArrayList<String>> labelNameLists = setUpLabelNameList(distinctCategoryNames);
        ArrayList<ArrayList<CustomPicture>> customPictureLists = setUpCustomPicList(distinctCategoryNames);

        // Initialise recycler view
        initRecyclerView(distinctCategoryNames, customPictureLists);
    }

    /* This on touch method is for the add category button on the top tab */
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
                    switch (view.getId()) {
                        case R.id.information_button:
                            CustomInformationDialog myDialog = new CustomInformationDialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                            myDialog.show();
                            break;
                        case R.id.add_cat_button:
                            MyUtilities.printOutPTable(this.getActivity());
                            MyUtilities.printOutCTable(this.getActivity());
                            //Add a category
                            final Dialog nagDialog = new Dialog(getActivity());
                            //nagDialog.getWindow().getAttributes().windowAnimations = R.style.DialogScale;
                            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            nagDialog.setContentView(R.layout.dialog_insert_cat_name);
                            nagDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);


                            final EditText catNameET = (EditText) nagDialog.findViewById(R.id.editT1);
                            // Automatically bring up keyboard
                            catNameET.requestFocus();
                            nagDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            //Set add category button on click listener
                            Button submitButton = (Button) nagDialog.findViewById(R.id.button1);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText categoryNameText = (EditText) nagDialog.findViewById(R.id.editT1);
                                    String newCategoryName = categoryNameText.getText().toString().trim();
                                    if (!MyUtilities.hasDuplicatedCatNames(newCategoryName, recyclerViewAdaptor.categoryNames)) {
                                        // Update recycler view
                                        recyclerViewAdaptor.categoryNames.add(newCategoryName);
                                        recyclerViewAdaptor.customPicsLists.add(new ArrayList<CustomPicture>());
                                        int currentIndex = recyclerViewAdaptor.categoryNames.indexOf(newCategoryName);
                                        recyclerViewAdaptor.notifyItemInserted(currentIndex);
                                        nagDialog.dismiss();

                                        MyUtilities.createOneTimeIntroDialog(getActivity(), "first_time_page4", R.drawable.starting_dialog4);
                                    } else {
                                        Toast.makeText(getActivity(), "Unable to add label, you already have an exact label name", Toast.LENGTH_LONG).show();
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
                    }
                }
        }
        return true;
    }

    /* This on drag is for detecting drop for the remove picture top bar */
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // view -> topTabSpace2
        PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f);
        PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f);
        PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);

        PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.5f);
        PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.5f);
        PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Remove
                ObjectAnimator.ofPropertyValuesHolder(topTabSpace, alphaDown).start();
                ObjectAnimator.ofPropertyValuesHolder(addCatButton, alphaDown).start();
                ObjectAnimator.ofPropertyValuesHolder(informationButton, alphaDown).start();
                ObjectAnimator.ofPropertyValuesHolder(appNameTV, alphaDown).start();

                // Appear
                ObjectAnimator.ofPropertyValuesHolder(dustbinTV, scaleXUp, scaleYUp, alphaUp).start();
                ObjectAnimator.ofPropertyValuesHolder(topTabSpace2, alphaUp).start();

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                // Remove
                ObjectAnimator.ofPropertyValuesHolder(dustbinTV, scaleXDown, scaleYDown, alphaDown).start();
                ObjectAnimator.ofPropertyValuesHolder(topTabSpace2, alphaDown).start();

                // Appear
                ObjectAnimator.ofPropertyValuesHolder(topTabSpace, alphaUp).start();
                ObjectAnimator.ofPropertyValuesHolder(addCatButton, alphaUp).start();
                ObjectAnimator.ofPropertyValuesHolder(informationButton, alphaUp).start();
                ObjectAnimator.ofPropertyValuesHolder(appNameTV, alphaUp).start();

                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                PropertyValuesHolder textSizeUpX = PropertyValuesHolder.ofFloat(TextView.SCALE_X, 1f, 1.2f);
                PropertyValuesHolder textSizeUpY = PropertyValuesHolder.ofFloat(TextView.SCALE_Y, 1f, 1.2f);
                ObjectAnimator.ofPropertyValuesHolder(dustbinTV, textSizeUpX, textSizeUpY).start();
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                PropertyValuesHolder textSizeDownX = PropertyValuesHolder.ofFloat(TextView.SCALE_X, 1.2f, 1f);
                PropertyValuesHolder textSizeDownY = PropertyValuesHolder.ofFloat(TextView.SCALE_Y, 1.2f, 1f);
                ObjectAnimator.ofPropertyValuesHolder(dustbinTV, textSizeDownX, textSizeDownY).start();
                break;
            case DragEvent.ACTION_DROP:
                dustbinTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                Log.d(TAG, "onDrag: Drop detected at remove bar");

                /* Don't need to do this since recyclerview removed the dragged picture already after drag
                started. If dropped anywhere else except top remove bar, it will add back to original grid
                // User wants to delete the photo
                // Update recycler view
                int currentIndex = recyclerViewAdaptor.categoryNames.indexOf(draggedPicture.getCatName());
                recyclerViewAdaptor.photoPathLists.get(currentIndex).remove(draggedPicture.getPhotoPath());
                recyclerViewAdaptor.notifyItemChanged(currentIndex);
                */

                // Refresh middle page
                ActivityMain.swipeAdaptor.getItem(0).onResume();
                break;
            default:
                break;
        }
        return true;
    }








    /* HELPER METHODS */

    // Set up distinctCategoryNames list
    public ArrayList<String> setUpDistinctCategoryNamesList() {
        ArrayList<String> distinctCategoryNames = new ArrayList<>();
      /*  Cursor res3 = mydb.getAllDataCTable();

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
*/
        Cursor res1 = mydb.getAllDataCTable();
        while (res1.moveToNext()) {
            String currentCatName = res1.getString(1);
            distinctCategoryNames.add(currentCatName);
        }

        // If dun have unsorted category
        if (!distinctCategoryNames.contains(ActivityMain.DEFAULTCATEGORYNAME)) {
            distinctCategoryNames.add(0, ActivityMain.DEFAULTCATEGORYNAME);
        }

        return distinctCategoryNames;
    }

    // Set up photopath lists
    public ArrayList<ArrayList<String>> setUpPhotoPathList(ArrayList<String> distinctCategoryNames) {
        ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
        for (int i = 0; i < distinctCategoryNames.size(); i++) {
            Cursor res2 = mydb.getBasedOnCategoryPTable(distinctCategoryNames.get(i));
            ArrayList<String> photoPaths = new ArrayList<>();
            while (res2.moveToNext()) {
                photoPaths.add(res2.getString(1));
            }
            photoPathLists.add(photoPaths);
        }
        return photoPathLists;
    }

    // Set up photopath lists
    public ArrayList<ArrayList<CustomPicture>> setUpCustomPicList(ArrayList<String> distinctCategoryNames) {
        ArrayList<ArrayList<CustomPicture>> customPicsLists = new ArrayList<>();
        for (int i = 0; i < distinctCategoryNames.size(); i++) {
            Cursor res2 = mydb.getBasedOnCategoryPTable(distinctCategoryNames.get(i));
            ArrayList<CustomPicture> customPics = new ArrayList<>();
            while (res2.moveToNext()) {
                String photopathName = res2.getString(1);
                String labelName = res2.getString(3);
                String catName = distinctCategoryNames.get(i);
                int year = res2.getInt(5);
                int month = res2.getInt(6);
                int day = res2.getInt(7);
                int hour = res2.getInt(8);
                int mins = res2.getInt(9);
                int secs = res2.getInt(10);
                customPics.add(new CustomPicture(getActivity(), photopathName, labelName, catName, null,
                        year, month, day, hour, mins, secs));
            }
            customPicsLists.add(customPics);
        }
        return customPicsLists;
    }

    // Set up label name lists
    public ArrayList<ArrayList<String>> setUpLabelNameList(ArrayList<String> distinctCategoryNames) {
        ArrayList<ArrayList<String>> labelNameLists = new ArrayList<>();
        for (int i = 0; i < distinctCategoryNames.size(); i++) {
            Cursor res2 = mydb.getBasedOnCategoryPTable(distinctCategoryNames.get(i));
            ArrayList<String> labelNames = new ArrayList<>();
            while (res2.moveToNext()) {
                labelNames.add(res2.getString(3));
            }
            labelNameLists.add(labelNames);
        }
        return labelNameLists;
    }

    public void initRecyclerView(ArrayList<String> distinctCategoryNames, ArrayList<ArrayList<CustomPicture>> customPicsLists) {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        recyclerViewAdaptor = new RecyclerViewAdaptor(this.getActivity(), distinctCategoryNames, customPicsLists);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public RecyclerViewAdaptor getRecyclerViewAdaptor() {
        return recyclerViewAdaptor;
    }
}
