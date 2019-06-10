package com.example.franc.testcamera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by franc on 1/6/2019.
 */

public class SwipeAdaptor extends FragmentStatePagerAdapter {
    private int numPages = 3;

    public SwipeAdaptor(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = new FragmentPage0();
        }
        else if (position == 1) {
            fragment = new FragmentPage1();
        }
        else {
            fragment = new FragmentPage2();
        }

        //Put in information that you need into bundles which is shared by all fragment pages
        Bundle bundle = new Bundle();
        bundle.putInt("count", position + 1);  //Since position starts from 0
        fragment.setArguments(bundle);

        return fragment;
    }

    //How many pages we want
    @Override
    public int getCount() {
        return numPages;
    }

}
