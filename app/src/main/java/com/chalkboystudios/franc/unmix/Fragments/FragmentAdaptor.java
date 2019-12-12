package com.chalkboystudios.franc.unmix.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.chalkboystudios.franc.unmix.Fragments.FragmentPageGallery;
import com.chalkboystudios.franc.unmix.Fragments.FragmentPageMain;

/**
 * Created by franc on 1/6/2019.
 */

public class FragmentAdaptor extends FragmentStatePagerAdapter {
    private int numPages = 2;
    private FragmentPageGallery fragment2;
    private FragmentPageMain fragmentmiddle;

    public FragmentAdaptor(FragmentManager fm) {
        super(fm);
        fragmentmiddle = new FragmentPageMain();
        fragment2 = new FragmentPageGallery();
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
