package com.example.franc.testcamera;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.franc.testcamera.Fragments.FragmentPage2;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 27/6/2019.
 */

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
    private FragmentPage2 fragment;
    private List<String> categoryNames = new ArrayList<>();
    private ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
    private Context myContext;

    private PicturesDatabaseHelper mydb;


    public RecyclerViewAdaptor(FragmentPage2 fragment, List<String> categoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        this.fragment = fragment;
        this.categoryNames = categoryNames;
        this.photoPathLists = photoPathLists;
        this.myContext = fragment.getActivity();
        mydb = new PicturesDatabaseHelper(myContext);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set label names
        holder.categoryName.setText(categoryNames.get(position));

        // Set grid view pictures
        ArrayList<String> currentPhotoPathList = photoPathLists.get(position);

        for (final String currentPhotoPath : currentPhotoPathList) {
            // Render images
            ImageView newImageView = new ImageView(fragment.getContext());
            newImageView.setAdjustViewBounds(true);
            int gridWidth = fragment.getResources().getDisplayMetrics().widthPixels;
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(gridWidth / 3, gridWidth / 3);
            newImageView.setPadding(14, 14, 14, 14);
            newImageView.setLayoutParams(lp1);

            // OMG GLIDE DOES IMAGE LOADING SO MUCH BETTER!! No lags due to decode file
            Glide
                    .with(myContext)
                    .load(currentPhotoPath)
                    .transform(new CenterCrop(), new RoundedCorners(15))
                    .into(newImageView);

            holder.gridLayout.addView(newImageView);

            newImageView.setTag(currentPhotoPath);
            newImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData data = ClipData.newPlainText("", "");
                    //View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                }
            });

            newImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        /*
                        PopupMenu popupMenu = new PopupMenu(fragment.getActivity(), newImageView);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                return true;
                            }
                        });
                        popupMenu.inflate(R.menu.pop_menu);
                        popupMenu.show();
                        */

                    final Dialog nagDialog = new Dialog(myContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                    nagDialog.setContentView(R.layout.preview_image_page);

                    ImageView previewImage = (ImageView) nagDialog.findViewById(R.id.preview_image);
                    Glide
                            .with(myContext)
                            .load(currentPhotoPath)
                            .into(previewImage);

                    nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        TextView categoryName;
        GridLayout gridLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
            categoryName = (TextView) itemView.findViewById(R.id.label_name);
            gridLayout = (GridLayout) itemView.findViewById(R.id.grid1);

            parentLayout.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    ImageView draggedImage = (ImageView) event.getLocalState();
                    GridLayout oldGridView = (GridLayout) draggedImage.getParent();        // v -> parentlayout
                    GridLayout newGridView = (GridLayout) ((LinearLayout)v).getChildAt(1);      // view -> the dragged picture

                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            draggedImage.setVisibility(View.INVISIBLE);
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            draggedImage.setVisibility(View.VISIBLE);
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            break;
                        case DragEvent.ACTION_DROP:
                            oldGridView.removeView(draggedImage);
                            newGridView.addView(draggedImage);
                            boolean hasInsertedData = mydb.updateCategoryNameData((String) draggedImage.getTag(), categoryName.getText().toString());
                            if (hasInsertedData) {
                                Toast.makeText(myContext, "Successfully updated cat name", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(myContext, "Error updating cat name", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
        }
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
            width = getView().getWidth() / 2;

            // Sets the height of the shadow to half the height of the original View
            height = getView().getHeight() / 2;

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
