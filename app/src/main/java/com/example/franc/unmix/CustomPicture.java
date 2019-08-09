package com.example.franc.unmix;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.franc.unmix.Fragments.FragmentPage2;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;

/**
 * Created by franc on 2/7/2019.
 */

public class CustomPicture extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private ImageView newImageView;
    private RelativeLayout whiteSpace;
    private TextView labelNameTVN;
    private String photoPath;
    private String currentLabelName;
    private PicturesDatabaseHelper mydb;
    private TextView categoryTV;
    private ImageView line;

    private int customPictureLength;
    private int whiteSpaceHeight;
    private static final int picturePadding = 7;

    public CustomPicture(Context context, String photoPath) {
        super(context);
        this.context = context;
        this.photoPath = photoPath;

        // Initialising picture properties
        int gridWidth = context.getResources().getDisplayMetrics().widthPixels;
        customPictureLength = gridWidth / 4;
        whiteSpaceHeight = customPictureLength / 3;

        // Get the label for this custom picture
        /*mydb = new PicturesDatabaseHelper(context);
        Cursor res = mydb.getLabelFromPathPTable(photoPath);
        res.moveToNext();
        currentLabelName = res.getString(3);*/
        currentLabelName = null;

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

        /* Set up label overlay */
        if (currentLabelName != null) {
            // Add a layer to show each picture label
            // Set up white space to show label (normal mode)
            whiteSpace = new RelativeLayout(context);
            whiteSpace.setBackground(context.getResources().getDrawable(R.drawable.white_rectangle));
            whiteSpace.getBackground().setAlpha(190);

            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, whiteSpaceHeight);
            lp3.setMargins(0, customPictureLength - whiteSpaceHeight, 0, 0);
            whiteSpace.setLayoutParams(lp3);
            this.addView(whiteSpace);

            // Set up label name
            labelNameTVN = new TextView(context);
            labelNameTVN.setTag("textview");
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            labelNameTVN.setLayoutParams(lp2);
            labelNameTVN.setGravity(Gravity.CENTER);
            labelNameTVN.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            labelNameTVN.setTypeface(labelNameTVN.getTypeface(), Typeface.BOLD_ITALIC);
            whiteSpace.addView(labelNameTVN);
            labelNameTVN.setText(currentLabelName);
        }
        /* If there is no label name */
        else {
            // Add a layer to show each picture label
            // Set up white space to show label (normal mode)
            whiteSpace = new RelativeLayout(context);
            whiteSpace.setBackground(context.getResources().getDrawable(R.drawable.white_rectangle));
            whiteSpace.getBackground().setAlpha(80);

            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, whiteSpaceHeight);
            lp3.setMargins(0, customPictureLength - whiteSpaceHeight, 0, 0);
            whiteSpace.setLayoutParams(lp3);
            this.addView(whiteSpace);

            // Set up label name
            labelNameTVN = new TextView(context);
            labelNameTVN.setTag("textview");
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            labelNameTVN.setLayoutParams(lp2);
            labelNameTVN.setGravity(Gravity.CENTER);
            labelNameTVN.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            labelNameTVN.setTypeface(labelNameTVN.getTypeface(), Typeface.BOLD_ITALIC);
            whiteSpace.addView(labelNameTVN);
        }
    }

    // Must be run after displayPicture() is called
    public void setCustomListener(TextView categoryTV, ImageView line) {
        this.categoryTV = categoryTV;
        this.line = line;
        this.setOnClickListener(this);
        /* If label name exist set listener */
        if (labelNameTVN != null) {
            labelNameTVN.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        /* Listener for imageview */
        if (v.getTag() == null) {
            // Show preview image function
            final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            nagDialog.setContentView(R.layout.dialog_preview_image);

            ImageView previewImage = (ImageView) nagDialog.findViewById(R.id.preview_image);
            previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("clicked preview layout");
                    // Show action bar
                }
            });
            Glide
                    .with(context)
                    .load(photoPath)
                    .into(previewImage);
            nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        }

        /* Listener for label name textview */
        else if (v.getTag().equals("textview")) {
            // Edit label function
            final Dialog nagDialog = new Dialog(context);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setContentView(R.layout.dialog_edit_label_name);

            final EditText labelNameET = (EditText) nagDialog.findViewById(R.id.editT4);
            labelNameET.setText(currentLabelName);

            //Set add category button on click listener
            Button submitButton = (Button) nagDialog.findViewById(R.id.button4);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newLabelName = labelNameET.getText().toString().trim();
                    nagDialog.dismiss();

                    // Note: dont need to update database since detached is called
                    currentLabelName = newLabelName;
                    ActivityMain.swipeAdaptor.getItem(2).onResume();
                }
            });
            nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    nagDialog.dismiss();
                }
            });
            nagDialog.show();
        }
    }

    // Must be run after displayPicture() is called
    public String getPhotoPath() {
        return this.photoPath;
    }

    // Must be run after displayPicture() is called
    public String getLabelName() {
        return this.currentLabelName;
    }

    // Must be run after displayPicture() is called
    public String getCatName() {
        return this.categoryTV.getText().toString();
    }

}
