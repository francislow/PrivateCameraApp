package com.chalkboystudios.franc.unmix.Utilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.chalkboystudios.franc.unmix.Fragments.FragmentPage2;
import com.chalkboystudios.franc.unmix.Fragments.FragmentPageMiddle;

/**
 * Created by franc on 1/6/2019.
 */

public class SwipeAdaptor extends FragmentStatePagerAdapter {
    private int numPages = 2;
    private FragmentPage2 fragment2;
    private FragmentPageMiddle fragmentmiddle;

    public SwipeAdaptor(FragmentManager fm) {
        super(fm);
        fragmentmiddle = new FragmentPageMiddle();
        fragment2 = new FragmentPage2();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = fragmentmiddle;
        }
        else {
            fragment = fragment2;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return numPages;
    }
}
