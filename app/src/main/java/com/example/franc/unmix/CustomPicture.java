package com.example.franc.unmix;

import android.content.Context;
import android.database.Cursor;
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
import com.example.franc.unmix.Fragments.FragmentPage2;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;

/**
 * Created by franc on 2/7/2019.
 */

public class CustomPicture extends RelativeLayout {
    private Context context;
    private ImageView newImageView;
    private TextView labelNameTV;

    public CustomPicture(Context context) {
        super(context);

        init(context);
    }

    public void init(Context context) {
        this.context = context;

        int gridWidth = context.getResources().getDisplayMetrics().widthPixels;

        // Set up linear layout
        ViewGroup.LayoutParams lp1 = new ViewGroup.LayoutParams(gridWidth / 4, gridWidth / 4);
        this.setLayoutParams(lp1);
        this.setPadding(7, 7, 7, 7);

        // Set up image view
        newImageView = new ImageView(context);
        newImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.addView(newImageView);

        // Set up label name
        labelNameTV = new TextView(context);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        labelNameTV.setLayoutParams(lp2);
        labelNameTV.setGravity(Gravity.CENTER);
        labelNameTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP,14);
        this.addView(labelNameTV);
    }

    public void displayPicture(String photoPath) {
        Glide
                .with(context)
                .load(photoPath)
                .transform(new CenterCrop(), new RoundedCorners(15))
                .into(newImageView);

        // If it is currently in labelview
        if (FragmentPage2.labelViewFlag) {
            newImageView.setAlpha(50);

            PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(context);
            Cursor res = mydb.getLabelFromPathPTable(photoPath);
            res.moveToNext();
            String currentLabel = res.getString(3);

            // If picture has a label
            if (currentLabel != null) {
                labelNameTV.setText(currentLabel);
            }
        }
    }
}
