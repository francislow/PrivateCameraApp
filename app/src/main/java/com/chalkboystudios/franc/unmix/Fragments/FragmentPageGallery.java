package com.chalkboystudios.franc.unmix.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chalkboystudios.franc.unmix.ActivityMain;
import com.chalkboystudios.franc.unmix.RecyclerViewAdaptors.RecyclerViewAdaptorGallery;
import com.chalkboystudios.franc.unmix.CustomWidgets.CustomInformationDialog;
import com.chalkboystudios.franc.unmix.CustomWidgets.CustomPicture;
import com.chalkboystudios.franc.unmix.R;
import com.chalkboystudios.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.chalkboystudios.franc.unmix.Utilities.MyUtilities;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageGallery extends Fragment implements View.OnTouchListener, View.OnDragListener {
    private PicturesDatabaseHelper mydb;
    private Button addCatButton;
    private Button informationButton;
    private TextView appNameTV;
    private RelativeLayout topTabSpace;
    private RelativeLayout topTabSpaceRemove;
    private TextView dustbinTV;
    private RecyclerView recyclerView;
    private RecyclerViewAdaptorGallery recyclerViewAdaptorGallery;

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
        topTabSpaceRemove = (RelativeLayout) getActivity().findViewById(R.id.top_tab_space_2);
        topTabSpaceRemove.setOnDragListener(this);
        topTabSpaceRemove.setAlpha(0);

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
        Log.d(TAG, "onPause: FragmentPageGallery");

        recyclerView.setAdapter(null);
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: FragmentPageGallery");

        // Get data from database
        ArrayList<String> distinctCategoryNames = setUpDistinctCategoryNamesList();
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
                                    if (!MyUtilities.hasDuplicatedCatNames(newCategoryName, recyclerViewAdaptorGallery.categoryNames)) {
                                        // Update recycler view
                                        recyclerViewAdaptorGallery.categoryNames.add(newCategoryName);
                                        recyclerViewAdaptorGallery.customPicsLists.add(new ArrayList<CustomPicture>());
                                        int currentIndex = recyclerViewAdaptorGallery.categoryNames.indexOf(newCategoryName);
                                        recyclerViewAdaptorGallery.notifyItemInserted(currentIndex);
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
                ObjectAnimator.ofPropertyValuesHolder(topTabSpaceRemove, alphaUp).start();

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                // Remove
                ObjectAnimator.ofPropertyValuesHolder(dustbinTV, scaleXDown, scaleYDown, alphaDown).start();
                ObjectAnimator.ofPropertyValuesHolder(topTabSpaceRemove, alphaDown).start();

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

                // Delete actual file stored in package
                MyUtilities.deleteFile(draggedPicture.getPhotoPath());
                /* Don't need to do this since recyclerview removed the dragged picture already after drag
                started. If dropped anywhere else except top remove bar, it will add back to original grid
                // User wants to delete the photo
                // Update recycler view
                int currentIndex = recyclerViewAdaptorGallery.categoryNames.indexOf(draggedPicture.getCatName());
                recyclerViewAdaptorGallery.photoPathLists.get(currentIndex).remove(draggedPicture.getPhotoPath());
                recyclerViewAdaptorGallery.notifyItemChanged(currentIndex);
                */

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
        Cursor res1 = mydb.getAllDataCTable();
        while (res1.moveToNext()) {
            String currentCatName = res1.getString(1);
            distinctCategoryNames.add(currentCatName);
        }

        // If dun have unsorted category
        if (!distinctCategoryNames.contains(ActivityMain.DEFAULT_CAT_NAME)) {
            distinctCategoryNames.add(0, ActivityMain.DEFAULT_CAT_NAME);
        }

        return distinctCategoryNames;
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
                int min = res2.getInt(9);
                int sec = res2.getInt(10);
                customPics.add(new CustomPicture(getActivity(), photopathName, labelName, catName, null,
                        year, month, day, hour, min, sec));
            }
            customPicsLists.add(customPics);
        }
        return customPicsLists;
    }

    public void initRecyclerView(ArrayList<String> distinctCategoryNames, ArrayList<ArrayList<CustomPicture>> customPicsLists) {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        recyclerViewAdaptorGallery = new RecyclerViewAdaptorGallery(this.getActivity(), distinctCategoryNames, customPicsLists);
        recyclerView.setAdapter(recyclerViewAdaptorGallery);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
