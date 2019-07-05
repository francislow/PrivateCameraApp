package com.example.franc.unmix;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private Button widthButton;
    private Button heightButton;
    private TextView tv;
    private LinearLayout noteToolBar;

    public StickyNoteWidget(Context context, ViewGroup layout, Cursor res) {
        this.context = context;
        this.layout = layout;
        this.number = res.getInt(0);
        this.title = res.getString(1);
        this.des = res.getString(2);
        this.lMargin = res.getInt(3);
        this.rMargin = res.getInt(4);
        this.tMargin = res.getInt(5);
        this.bMargin = res.getInt(6);
        this.width = res.getInt(7);
        this.height = res.getInt(8);
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
        tv = new TextView(context);
        tv.setText(number + " " + title + " " + des + "\n");
        tv.setBackgroundResource(R.color.green);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(0.85* height));
        tv.setLayoutParams(lp2);
        snwLayout.addView(tv);   //add children to snw model

        //Sticky Note - Toolbar
        noteToolBar = new LinearLayout(context);
        noteToolBar.setOrientation(LinearLayout.HORIZONTAL);
        noteToolBar.setBackgroundResource(R.color.blue);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                setWidth(width - 150);
                setHeight(height - 150);
                snwLayout.invalidate();
                tv.invalidate();
                heightButton.invalidate();
                widthButton.invalidate();

            }
        });
        heightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWidth(width + 150);
                setHeight(height + 150);
                snwLayout.invalidate();
                tv.invalidate();
                heightButton.invalidate();
                widthButton.invalidate();
            }
        });
        heightButton.setActivated(false);
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

    public void setSize(int newWidth, int newHeight) {
        this.height = newHeight;
        this.width = newWidth;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
        snwLayout.setLayoutParams(lp);
    }
}
