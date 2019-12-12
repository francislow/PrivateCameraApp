package com.chalkboystudios.franc.unmix.Fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chalkboystudios.franc.unmix.ActivityMain;
import com.chalkboystudios.franc.unmix.RecyclerViewAdaptors.RecyclerViewAdaptorMain;
import com.chalkboystudios.franc.unmix.Utilities.PicturesDatabaseHelper;
import com.chalkboystudios.franc.unmix.R;
import com.chalkboystudios.franc.unmix.Utilities.MyUtilities;
import com.chalkboystudios.franc.unmix.Utilities.RequestCodeHelper;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPageMain extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private LinearLayout LLOfThumbnails;
    private ScrollView vScrollView;
    private int screenHeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page_middle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get screen width and height
        screenHeight = getActivity().getResources().getDisplayMetrics().heightPixels;

        //LinearLayout
        LLOfThumbnails = (LinearLayout) getActivity().findViewById(R.id.LL2);
        vScrollView = (ScrollView) getActivity().findViewById(R.id.vScrollView);
        // To fix bug where scroll view is hidden on resume
        vScrollView.smoothScrollTo(0, 0);

        //Setup Top Tab
        //Camera Button
        final Button camButton = (Button) getActivity().findViewById(R.id.cambutton);
        camButton.setOnTouchListener(this);

        //Photo Button
        final Button addButton = (Button) getActivity().findViewById(R.id.addButton);
        addButton.setOnTouchListener(this);

        MyUtilities.createOneTimeIntroDialog(getActivity(), "first_time_page1", R.drawable.starting_dialog1);

        // Create initial app intro dialog
        String prefKey = "intro_app_pref_key";
        Log.d(TAG, "createOneTimeIntroDialog: tried running one time dialog");
        SharedPreferences prefs = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE);
        boolean first_time_flag = prefs.getBoolean(prefKey, true);//"No name defined" is the default value.

        if (first_time_flag) {
            // if no entry add first time flag  = false as entry
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(prefKey, false);
            editor.apply();

            // if first time starting app, apply dialog
            final Dialog myDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            // Set Layout
            myDialog.setContentView(R.layout.dialog_intro_app);

            // Set dialog background to transparent
            myDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

            // Set appear and disappear transitions
            myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFade;

            // Set unable to use back button to cancel
            myDialog.setCancelable(false);

            Button cancelDialogButton = (Button) myDialog.findViewById(R.id.cancel_dialog_button);
            cancelDialogButton.setVisibility(View.VISIBLE);
            cancelDialogButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1f);
                    PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1f);
                    PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f);

                    PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.7f);
                    PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.7f);
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
                                myDialog.dismiss();
                            }
                    }
                    return true;
                }
            });
            myDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Remove all pictures and thumbnails
        LLOfThumbnails.removeAllViews();

        // Render all pictures and thumbnails
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(getActivity());
        final Cursor res = mydb.getAllDataPTable();

        // Set up picture info list
        ArrayList<PictureInfo> pictureInfoList = new ArrayList<>();
        while (res.moveToNext()) {
            //Get current photo path
            String currentPhotoPath = res.getString(1);
            int year = res.getInt(5);
            int month = res.getInt(6);
            int day = res.getInt(7);
            int hour = res.getInt(8);
            int min = res.getInt(9);
            int sec = res.getInt(10);

            pictureInfoList.add(0, new PictureInfo(currentPhotoPath, year, month, day, hour, min, sec));
        }

        // Sort according to age
        Collections.sort(pictureInfoList);
        initRecyclerView(pictureInfoList);

        //Set up thumbnail (image view)
        for (int i = 0; i < pictureInfoList.size(); i++) {
            String currentPhotoPath = pictureInfoList.get(i).getPhotopath();
            ImageView newThumbnail = new ImageView(this.getActivity());
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(screenHeight / 13, screenHeight / 13);
            lp2.setMargins(23, 10, 0, 10);
            newThumbnail.setLayoutParams(lp2);
            LLOfThumbnails.addView(newThumbnail);
            Glide
                    .with(getActivity())
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(60))
                    .into(newThumbnail);
            //Set on click listener
            newThumbnail.setOnClickListener(this);
        }
    }

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

                // If user's touch up is still in the button
                if (MyUtilities.touchUpInButton(motionEvent, (Button) view)) {
                    switch (view.getId()) {
                        // If it is a camera button
                        case R.id.cambutton:
                            // Start camera
                            ActivityMain.startCamera();
                            break;

                        // If it is an add from gallery button
                        case R.id.addButton:
                            // Start gallery
                            Intent goToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                            getActivity().startActivityForResult(goToGallery, RequestCodeHelper.PICK_IMAGE_REQUEST);
                            break;
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        ImageView currentThumbnail = (ImageView) view;
        int indexOfThumbNail = LLOfThumbnails.indexOfChild(currentThumbnail);
        TextView tv = (TextView) getActivity().findViewById(R.id.recentlyadded);
        vScrollView.smoothScrollTo(0, indexOfThumbNail * MyUtilities.convertDpToPx(getContext(), 400) + tv.getHeight());

    }

    public void initRecyclerView(ArrayList<PictureInfo> pictureInfoList) {
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerv2);
        // Allow smooth gliding scrolling
        recyclerView.setNestedScrollingEnabled(false);
        // Set recycler view adaptor
        RecyclerViewAdaptorMain myRVA = new RecyclerViewAdaptorMain(getActivity(), pictureInfoList);
        recyclerView.setAdapter(myRVA);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public class PictureInfo implements Comparable<PictureInfo> {
        private String photopath;
        private int yr;
        private int mnth;
        private int day;
        private int hour;
        private int min;
        private int sec;

        private PictureInfo(String photopath, int yr, int mnth, int day, int hour, int min, int sec) {
            this.photopath = photopath;
            this.yr = yr;
            this.mnth = mnth;
            this.day = day;
            this.hour = hour;
            this.min = min;
            this.sec = sec;
        }

        public String getPhotopath() {
            return photopath;
        }

        private int getYr() {
            return yr;
        }

        private int getMnth() {
            return mnth;
        }

        private int getDay() {
            return day;
        }

        private int getHour() {
            return hour;
        }

        private int getMin() {
            return min;
        }

        private int getSec() {
            return sec;
        }

        @Override
        public int compareTo(@NonNull PictureInfo o) {
            Log.d(TAG, "compareTo: ran");
            if (yr < o.getYr()) {
                return 1;
            } else if (yr > o.getYr()) {
                return -1;
            } else {
                if (mnth < o.getMnth()) {
                    return 1;

                } else if (mnth > o.getMnth()) {
                    return -1;
                } else {
                    float minuteAge = (day * 24 * 60) + (hour * 60) + (min) + ((float) sec / 60);
                    float oMinuteAge = (o.getDay() * 24 * 60) + (o.getHour() * 60) + (o.getMin()) + ((float) o.getSec() / 60);

                    if (minuteAge < oMinuteAge) {
                        return 1;
                    } else if (minuteAge > oMinuteAge) {
                        return -1;
                    } else {
                        System.out.println("returned 0");
                        return 0;
                    }
                }
            }
        }
    }
}
