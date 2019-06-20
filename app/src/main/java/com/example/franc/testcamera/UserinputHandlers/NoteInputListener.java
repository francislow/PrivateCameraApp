package com.example.franc.testcamera.UserinputHandlers;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.franc.testcamera.StickyNoteWidget;

/**
 * Created by franc on 18/6/2019.
 */

public class NoteInputListener implements View.OnTouchListener {
    private int x;
    private int y;
    //private boolean hasMoved;
    long startTime;
    long stopTime;
    StickyNoteWidget snw;

    public NoteInputListener(StickyNoteWidget snw) {
        this.snw = snw;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int touchX = (int) motionEvent.getRawX();
        final int touchY = (int) motionEvent.getRawY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                x = touchX - lp1.leftMargin;
                y = touchY - lp1.topMargin;
                //hasMoved = false;
                startTime = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams lp2 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                lp2.leftMargin = touchX - x;
                lp2.topMargin = touchY - y;
                lp2.rightMargin = -250;
                lp2.bottomMargin = -250;

                view.setLayoutParams(lp2);
                snw.setPosition(lp2);

                //hasMoved = true;
                break;
            case MotionEvent.ACTION_UP:
                stopTime = System.currentTimeMillis();
                System.out.println(startTime);
                System.out.println(stopTime);
                if (stopTime - startTime < 100) {
                    System.out.println("less than 200ms");
                    //Bring note up ie enlarge note
                }
                else if (stopTime - startTime > 2000) {
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    view.setLayoutParams(new RelativeLayout.LayoutParams(lp.width + 200, lp.height + 200));
                    System.out.println("more than 2000");

                }

                /*
                if (hasMoved == false) {
                    System.out.println("has not moved");
                }
                */
                break;
        }
        view.invalidate();
        return true;
    }
}
