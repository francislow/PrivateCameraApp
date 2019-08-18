package com.chalkboystudios.franc.unmix.Utilities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by franc on 11/8/2019.
 */

public class MyAnimUtilities {
    public static void fadeIn(View view) {
        PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, alphaUp).start();
    }

    public static void fadeOut(View view) {
        PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);
        ObjectAnimator.ofPropertyValuesHolder(view, alphaDown).start();
    }
}
