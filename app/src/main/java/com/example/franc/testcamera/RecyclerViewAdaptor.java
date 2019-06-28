package com.example.franc.testcamera;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 27/6/2019.
 */

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
    private Context myContext;
    private List<String> categoryNames = new ArrayList<>();
    private ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();

    public RecyclerViewAdaptor(Context myContext, List<String> categoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        this.myContext = myContext;
        this.categoryNames = categoryNames;
        this.photoPathLists = photoPathLists;
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
            final ImageView newImageView = new ImageView(myContext);
            newImageView.setAdjustViewBounds(true);
            int gridWidth = myContext.getResources().getDisplayMetrics().widthPixels;
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(gridWidth / 3, gridWidth / 3);
            lp1.setMargins(100, 100, 100, 100);
            newImageView.setLayoutParams(lp1);

            // OMG GLIDE DOES IMAGE LOADING SO MUCH BETTER!! No lags due to decode file
            Glide
                    .with(myContext)
                    .load(currentPhotoPath)
                    .centerCrop()
                    .into(newImageView);

            holder.gridLayout.addView(newImageView);

            //Set on click listeners for images
            newImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    System.out.println("IT WORRRKKKKKED");
                    return true;
                }
            });

            newImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        }
    }
}
