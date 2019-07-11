package com.example.franc.unmix;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

public class CustomPicture extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener, View.OnDragListener {
    private Context context;
    private ImageView newImageView;
    private RelativeLayout whiteSpace;
    private TextView labelNameTV;
    private TextView labelNameTV2;
    private String photoPath;
    private String currentLabel;
    private PicturesDatabaseHelper mydb;
    private TextView categoryTV;
    private ImageView line;

    private int gridWidth;
    private int customPictureLength;
    private int whiteSpaceHeight;
    private static final int picturePadding = 7;

    public CustomPicture(Context context, String photoPath) {
        super(context);
        this.context = context;
        this.photoPath = photoPath;

        // Initialising picture properties
        gridWidth = context.getResources().getDisplayMetrics().widthPixels;
        customPictureLength = gridWidth / 4;
        whiteSpaceHeight = customPictureLength / 3;

        // Get the photopath for this custom picture
        mydb = new PicturesDatabaseHelper(context);
        Cursor res = mydb.getLabelFromPathPTable(photoPath);
        res.moveToNext();
        currentLabel = res.getString(3);

        // Set up custompicture (Relativelayout) width and height
        ViewGroup.LayoutParams lp1 = new ViewGroup.LayoutParams(customPictureLength, customPictureLength);
        this.setLayoutParams(lp1);
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
    }

    public void displayImageBasedOnMode() {
        // NORMAL MODE
        if (!FragmentPage2.ISINLABELVIEWMODE) {
            if (currentLabel != null) {
                // Add a layer to show each picture label
                // Set up white space to show label (normal mode)
                whiteSpace = new RelativeLayout(context);
                whiteSpace.setBackground(context.getResources().getDrawable(R.drawable.white_rectangle));
                whiteSpace.getBackground().setAlpha(150);

                RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, whiteSpaceHeight);
                lp3.setMargins(0, customPictureLength - whiteSpaceHeight, 0, 0);
                whiteSpace.setLayoutParams(lp3);
                this.addView(whiteSpace);

                // Set up label name (display mode)
                labelNameTV2 = new TextView(context);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                labelNameTV2.setLayoutParams(lp2);
                labelNameTV2.setGravity(Gravity.CENTER);
                labelNameTV2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                labelNameTV2.setTypeface(labelNameTV2.getTypeface(), Typeface.ITALIC);
                whiteSpace.addView(labelNameTV2);

                labelNameTV2.setText(currentLabel);
            }
        }
        // LABELVIEW MODE
        else {
            newImageView.setAlpha(100);
            // Set up label name (Edit mode)
            labelNameTV = new TextView(context);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            labelNameTV.setLayoutParams(lp2);
            labelNameTV.setGravity(Gravity.CENTER);
            labelNameTV.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            labelNameTV.setTypeface(labelNameTV.getTypeface(), Typeface.BOLD_ITALIC);
            this.addView(labelNameTV);
            // If picture has a label
            if (currentLabel != null) {
                labelNameTV.setText(currentLabel);
            }
        }
    }

    // Must be run after displayPicture() is called
    public void setCustomListener(TextView categoryTV, ImageView line) {
        this.categoryTV = categoryTV;
        this.line = line;
        this.setOnClickListener(this);
        this.setOnLongClickListener(this);
        this.setOnDragListener(this);
    }

    @Override
    public void onClick(View v) {
        // If in normal mode
        if (!FragmentPage2.ISINLABELVIEWMODE) {
            // Show preview image function
            final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            nagDialog.setContentView(R.layout.dialog_preview_image);
            ImageView previewImage = (ImageView) nagDialog.findViewById(R.id.preview_image);

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
        } else {
            // Edit label function
            //Add a category
            final Dialog nagDialog = new Dialog(context);
            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            nagDialog.setContentView(R.layout.dialog_edit_label_name);

            final EditText labelNameET = (EditText) nagDialog.findViewById(R.id.editT4);
            labelNameET.setText(currentLabel);

            //Set add category button on click listener
            Button submitButton = (Button) nagDialog.findViewById(R.id.button4);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newLabelName = labelNameET.getText().toString().trim();
                    PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(context);
                    boolean hasInsertedData = mydb.updateLabelNamePTable(getPhotoPath(), newLabelName);
                    if (hasInsertedData) {
                        Toast.makeText(context, "successfully updated label to database", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error updating label to database", Toast.LENGTH_LONG).show();
                    }
                    nagDialog.dismiss();
                    ActivityMain.swipeAdaptor.getItem(3).onResume();
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

    @Override
    public boolean onLongClick(View view) {
        if (FragmentPage2.ISINLABELVIEWMODE) {
            ClipData data = ClipData.newPlainText("", "");
            //View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);

            // Drag started

            // Set constants after drag started
            FragmentPage2.oldGridLayout = (GridLayout) view.getParent();
            FragmentPage2.draggedPicture = (CustomPicture) view;

            // Remove dragged picture form old layout
            FragmentPage2.oldGridLayout.removeView(view);

        }
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        // v -> each custom picture
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                System.out.println("picture drag started");
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                System.out.println("drag custom pic ended");
                if (event.getResult() == false) {
                    System.out.println("no drop detected");
                    // This is freakin weird, why would dragged pic have a parent only when its the oni child
                    if (FragmentPage2.draggedPicture.getParent() != null) {
                        ((GridLayout) FragmentPage2.draggedPicture.getParent()).removeView(FragmentPage2.draggedPicture);
                    }
                    FragmentPage2.oldGridLayout.addView(FragmentPage2.draggedPicture);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                System.out.println("entred picture frame");
                //newGridLayout.addView(draggedPicture, newGridLayout.indexOfChild(v));
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                System.out.println("exited picture frame");
                //newGridLayout.removeView(draggedPicture);
                break;
            case DragEvent.ACTION_DROP:
                categoryTV.setTypeface(Typeface.create(categoryTV.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                line.setVisibility(View.INVISIBLE);

                GridLayout newGridLayout = (GridLayout) v.getParent();
                newGridLayout.addView(FragmentPage2.draggedPicture, newGridLayout.indexOfChild(v));
                boolean hasInsertedData = mydb.updateCategoryNamePTable((String) FragmentPage2.draggedPicture.getPhotoPath(), categoryTV.getText().toString());
                if (hasInsertedData) {
                    Toast.makeText(context, "Successfully updated cat name", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error updating cat name", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        return true;

    }

    // Must be run after displayPicture() is called
    public String getPhotoPath() {
        return this.photoPath;
    }

    // Must be run after displayPicture() is called
    public String getLabel() {
        return this.currentLabel;
    }


    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private Point mScaleFactor;

        public MyDragShadowBuilder(View v) {
            super(v);
        }

        // Defines a callback that sends the drag shadow dimensions and touch point back to the system.
        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {
            // Defines local variables
            int width;
            int height;

            // Sets the width of the shadow to half the width of the original View
            width = getView().getWidth() * 2 / 5;

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight() * 2 / 5;

            // Sets the size parameter's width and height values. These get back to the system
            // through the size parameter.
            size.set(width, height);
            // Sets size parameter to member that will be used for scaling shadow image.
            mScaleFactor = size;

            // Sets the touch point's position to be in the middle of the drag shadow
            touch.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow(Canvas canvas) {
            // Draws the ColorDrawable in the Canvas passed in from the system.
            canvas.scale(mScaleFactor.x / (float) getView().getWidth(), mScaleFactor.y / (float) getView().getHeight());
            getView().draw(canvas);
        }
    }
}
