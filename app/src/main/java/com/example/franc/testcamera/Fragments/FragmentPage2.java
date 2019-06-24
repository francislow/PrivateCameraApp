package com.example.franc.testcamera.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.R;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        return view;
    }

    @Override
    public void onPause() {
        System.out.println("fragmentpage2 PAUSED");
        super.onPause();
    }

    @Override
    public void onResume() {
        ActivityMain.lastViewedFragItem = 2;
        System.out.println("fragmentpage2 RESUMED");
        super.onResume();
    }
}
