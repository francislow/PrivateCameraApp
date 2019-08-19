package com.chalkboystudios.franc.unmix;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chalkboystudios.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;

/**
 * Created by franc on 2/7/2019.
 */

/*
    Properties that can be changed
    1) Label name
    2) Cat name
    3) White space opacity
    4) Black indicators opacity

 */

public class CustomPicture extends RelativeLayout {
    private Context context;
    private ImageView newImageView;
    private RelativeLayout whiteSpace;
    private TextView labelNameTVN;
    private String photoPath;
    private String labelName;
    int year;
    int month;
    int day;
    int hour;
    int min;
    int sec;
    private PicturesDatabaseHelper mydb;
    private String categoryName;
    private RelativeLayout blackSpace;
    private RelativeLayout blackSpace2;

    private int customPictureLength;
    private int whiteSpaceHeight;
    private int blackSpaceWidth;

    private static final int picturePadding = 7;

    public CustomPicture(Context context, String photoPath, String labelName, String categoryName, String position,
                         int year, int month, int day, int hour, int min, int sec) {
        super(context);
        this.context = context;
        this.photoPath = photoPath;
        this.labelName = labelName;
        this.categoryName = categoryName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;

        // Initialising picture properties
        int gridWidth = context.getResources().getDisplayMetrics().widthPixels;
        customPictureLength = gridWidth / 4;
        whiteSpaceHeight = customPictureLength / 3;
        blackSpaceWidth = whiteSpaceHeight / 3;

        // Set up custompicture (Relativelayout) width and height
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(customPictureLength, customPictureLength);
        this.setLayoutParams(lp);
        this.setPadding(picturePadding, picturePadding, picturePadding, picturePadding);

        // Add an filled image view to fill whole of this custom picture
        newImageView = new ImageView(context);
        newImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(newImageView);
        Glide
                .with(context)
                .load(photoPath)
                .transform(new CenterCrop(), new RoundedCorners(15))
                .into(newImageView);

        /* Set up white space */
        whiteSpace = new RelativeLayout(context);
        whiteSpace.setBackground(context.getResources().getDrawable(R.drawable.white_rectangle));
        RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, whiteSpaceHeight);
        lp3.setMargins(0, customPictureLength - whiteSpaceHeight, 0, 0);
        whiteSpace.setLayoutParams(lp3);
        this.addView(whiteSpace);

        /* Set up label overlay */
        labelNameTVN = new TextView(context);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        labelNameTVN.setLayoutParams(lp2);
        labelNameTVN.setGravity(Gravity.CENTER);
        labelNameTVN.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        Typeface moonchildtf = ResourcesCompat.getFont(context, R.font.moonchild);
        labelNameTVN.setTypeface(moonchildtf);
        whiteSpace.addView(labelNameTVN);

        // Set up indicator overlay
        blackSpace = new RelativeLayout(context);
        blackSpace.setBackground(context.getResources().getDrawable(R.drawable.black_indicator));

        RelativeLayout.LayoutParams lpp = new RelativeLayout.LayoutParams(blackSpaceWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        blackSpace.setLayoutParams(lpp);
        this.addView(blackSpace);

        blackSpace2 = new RelativeLayout(context);
        blackSpace2.setBackground(context.getResources().getDrawable(R.drawable.black_indicator));

        RelativeLayout.LayoutParams lpp2 = new RelativeLayout.LayoutParams(blackSpaceWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        lpp2.setMargins(customPictureLength - blackSpaceWidth - picturePadding *2,0,0,0);
        blackSpace2.setLayoutParams(lpp2);
        this.addView(blackSpace2);

        //Set up detection range
    }

    // Must be run after displayPicture() is called
    public String getPhotoPath() {
        return this.photoPath;
    }

    // Must be run after displayPicture() is called
    public String getLabelName() {
        return this.labelName;
    }

    // Must be run after displayPicture() is called
    public String getCatName() {
        return this.categoryName;
    }

    public TextView getLabelNameTVN() {
        return labelNameTVN;
    }

    public RelativeLayout getWhiteSpace() {
        return whiteSpace;
    }

    public ImageView getNewImageView() {
        return newImageView;
    }

    public RelativeLayout getBlackSpace() {
        return blackSpace;
    }

    public RelativeLayout getBlackSpace2() {
        return blackSpace2;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}