package com.example.franc.testcamera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Getting stored infomation from bundle (shared by all fragments)
        Bundle bundle = getArguments();
        String message = Integer.toString(bundle.getInt("count"));

        View view = inflater.inflate(R.layout.fragment_page2, container, false);
        return view;
    }
}
