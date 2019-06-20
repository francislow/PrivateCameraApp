package com.example.franc.testcamera;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.franc.testcamera.UserinputHandlers.NoteInputListener;

/**
 * Created by franc on 20/6/2019.
 */

public class StickyNoteWidget {
    private Context context;
    private ViewGroup layout;
    private int number;
    private String title;
    private String des;
    private int lMargin;
    private int rMargin;
    private int tMargin;
    private int bMargin;
    private int width;
    private int height;

    private LinearLayout snwLayout;
    Button widthButton;
    Button heightButton;

    public StickyNoteWidget(Context context, ViewGroup layout, int number, String title, String des,
                            int lMargin, int rMargin, int tMargin, int bMargin, int width, int height) {
        this.number = number;
        this.title = title;
        this.des = des;
        this.context = context;
        this.layout = layout;
        this.lMargin = lMargin;
        this.rMargin = rMargin;
        this.tMargin = tMargin;
        this.bMargin = bMargin;
        this.width = width;
        this.height = height;
    }

    public void addViewGroup() {
        //Sticky Note
        snwLayout = new LinearLayout(context);
        snwLayout.setOrientation(LinearLayout.VERTICAL);
        snwLayout.setBackgroundResource(R.color.lightgrey);
        RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(width, height);
        lp1.setMargins(lMargin, tMargin, rMargin, bMargin);
        snwLayout.setLayoutParams(lp1);

        //Sticky Note - TextView (title and description)
        TextView tv = new TextView(context);
        tv.setText(number + " " + title + " " + des + "\n");
        tv.setBackgroundResource(R.color.green);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(width, (int)(0.85* height));
        snwLayout.addView(tv);   //add children to snw model

        //Sticky Note - Toolbar
        LinearLayout noteToolBar = new LinearLayout(context);
        noteToolBar.setOrientation(LinearLayout.HORIZONTAL);
        noteToolBar.setBackgroundResource(R.color.blue);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
        noteToolBar.setLayoutParams(lp3);
        snwLayout.addView(noteToolBar);    //add children to snw model

        //Sticky Note - Toolbar - Set Sticky Note Size Buttons
        widthButton = new Button (context);
        widthButton.setText("decrease width and height");
        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams((int)(0.5* width), ViewGroup.LayoutParams.MATCH_PARENT);
        widthButton.setLayoutParams(lp4);
        noteToolBar.addView(widthButton);

        heightButton = new Button (context);
        heightButton.setText("increase width and height");
        LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams((int)(0.5* width), ViewGroup.LayoutParams.MATCH_PARENT);
        heightButton.setLayoutParams(lp5);
        noteToolBar.addView(heightButton);

        //Add the whole sticky note to fragment
        layout.addView(snwLayout);
    }

    public void setListener() {
        snwLayout.setOnTouchListener(new NoteInputListener(this));
        widthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                setWidth(width - 100);
                setHeight(height - 100);
                snwLayout.invalidate();
                */
            }
        });
        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWidth(width + 100);
                setHeight(height + 100);
                snwLayout.invalidate();
            }
        });
    }

    public ViewGroup getViewGroup() {
        return snwLayout;
    }

    public void setPosition(RelativeLayout.LayoutParams lp) {
        snwLayout.setLayoutParams(lp);
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        snwLayout.setLayoutParams(lp);
    }

    public void setHeight(int newHeight) {
        this.height = newHeight;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        snwLayout.setLayoutParams(lp);
    }
}
