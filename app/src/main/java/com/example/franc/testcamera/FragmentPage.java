package com.example.franc.testcamera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentPage extends Fragment {
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        textView = (TextView) view.findViewById(R.id.mytv);
        Bundle bundle = getArguments();
        String message = Integer.toString(bundle.getInt("count"));
        textView.setText("This is the " + message);
        return view;
    }
}
