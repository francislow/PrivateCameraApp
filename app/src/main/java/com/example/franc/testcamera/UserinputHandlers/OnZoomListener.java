package com.example.franc.testcamera.UserinputHandlers;

import android.graphics.Matrix;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by franc on 18/6/2019.
 */

public class OnZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    private float currentScale = 1f;
    private Matrix matrix = new Matrix();
    private ImageView view;

    public OnZoomListener(ImageView view) {
        this.view = view;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        currentScale = currentScale * detector.getScaleFactor();
        currentScale = Math.max(0.1f, Math.min(currentScale, 5f));
        matrix.setScale(currentScale, currentScale);
        //view.setScaleX(currentScale);
        //view.setScaleY(currentScale);
        view.setImageMatrix(matrix);

        return true;
    }
}
