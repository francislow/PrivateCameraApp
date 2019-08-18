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
    /*private FragmentPage1 fragment1;*/
    private FragmentPage2 fragment2;
    private FragmentPageMiddle fragmentmiddle;

    public SwipeAdaptor(FragmentManager fm) {
        super(fm);
        /*fragment1 = new FragmentPage1();*/
        fragmentmiddle = new FragmentPageMiddle();
        fragment2 = new FragmentPage2();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = fragmentmiddle;
        } else {
            fragment = fragment2;
        }

        /*
        //Put in information that you need into bundles which is shared by all fragment pages
        Bundle bundle = new Bundle();
        bundle.putInt("count", position + 1);  //Since position starts from 0
        fragment.setArguments(bundle);
        */
        return fragment;
    }

    @Override
    public int getCount() {
        return numPages;
    }

}
