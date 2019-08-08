package com.example.franc.unmix.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
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

        // Initialise recycler view
        initRecyclerView(distinctCategoryNames, photoPathLists);
    }

    /* This on touch method is for the add category button on the top tab */
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
                        case R.id.information_button:
                            break;
                        case R.id.add_cat_button:
                            MyUtilities.printOutPTable(this.getActivity());
                            MyUtilities.printOutCTable(this.getActivity());
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
                                    if (!MyUtilities.hasDuplicatedCatNames(newCategoryName, recyclerViewAdaptor.categoryNames)) {
                                        // Update recycler view
                                        recyclerViewAdaptor.categoryNames.add(newCategoryName);
                                        recyclerViewAdaptor.photoPathLists.add(new ArrayList<String>());
                                        int currentIndex = recyclerViewAdaptor.categoryNames.indexOf(newCategoryName);
                                        recyclerViewAdaptor.notifyItemInserted(currentIndex);
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
                PropertyValuesHolder textSizeUp = PropertyValuesHolder.ofFloat(TextView.SCALE_X, 1f, 0.5f);
                dustbinTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                dustbinTV.setTypeface(dustbinTV.getTypeface(), Typeface.BOLD_ITALIC);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                dustbinTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                dustbinTV.setTypeface(Typeface.create(dustbinTV.getTypeface(), Typeface.ITALIC), Typeface.ITALIC);
                break;
            case DragEvent.ACTION_DROP:
                dustbinTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                dustbinTV.setTypeface(Typeface.create(dustbinTV.getTypeface(), Typeface.ITALIC), Typeface.ITALIC);

                /* Don't need to do this since recyclerview removed the dragged picture already after drag
                started. If dropped anywhere else except top remove bar, it will add back to original grid
                // User wants to delete the photo
                // Update recycler view
                int currentIndex = recyclerViewAdaptor.categoryNames.indexOf(draggedPicture.getCatName());
                recyclerViewAdaptor.photoPathLists.get(currentIndex).remove(draggedPicture.getPhotoPath());
                recyclerViewAdaptor.notifyItemChanged(currentIndex);
                */

                // Refresh middle page
                ActivityMain.swipeAdaptor.getItem(1).onResume();
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

    // Set up photopath list
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

    public void initRecyclerView(ArrayList<String> distinctCategoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv);
        recyclerViewAdaptor = new RecyclerViewAdaptor(this.getActivity(), distinctCategoryNames, photoPathLists);
        recyclerView.setAdapter(recyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
