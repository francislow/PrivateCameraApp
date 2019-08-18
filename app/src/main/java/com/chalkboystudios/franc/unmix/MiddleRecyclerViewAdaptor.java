package com.chalkboystudios.franc.unmix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chalkboystudios.franc.unmix.Fragments.FragmentPageMiddle;
import com.chalkboystudios.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 27/6/2019.
 */

public class MiddleRecyclerViewAdaptor extends RecyclerView.Adapter<MiddleRecyclerViewAdaptor.ViewHolder> {
    public ArrayList<FragmentPageMiddle.PictureInfo> pictureInfoList = new ArrayList<>();
    private Context myContext;
    // associatedFragment = FragmentPageMiddle
    private Fragment associatedFragment;
    private PicturesDatabaseHelper mydb;


    public MiddleRecyclerViewAdaptor(Context context, ArrayList<FragmentPageMiddle.PictureInfo> pictureInfoList) {
        this.myContext = context;
        this.pictureInfoList = pictureInfoList;

        associatedFragment = ActivityMain.swipeAdaptor.getItem(0);
        mydb = new PicturesDatabaseHelper(myContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_page_middle_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ran Middle recyclerview");

        ImageView currentIV = holder.displayImage;
        final String currentPhotoPathName = pictureInfoList.get(position).getPhotopath();

        // Set up display image
        Glide
                .with(myContext)
                .load(currentPhotoPathName)
                .transform(new CenterCrop())
                .into(currentIV);

        //Set on click listener for the image view
        holder.displayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeout = new AlphaAnimation(1.f, 0.5f);
                fadeout.setDuration(300);
                v.startAnimation(fadeout);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Show preview image function
                        final Dialog nagDialog = new Dialog(myContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                        nagDialog.setContentView(R.layout.dialog_preview_image);

                        // Button
                        Button sendButton = (Button) nagDialog.findViewById(R.id.sendButton);
                        sendButton.setVisibility(View.GONE);

                        ImageView previewImage = (ImageView) nagDialog.findViewById(R.id.preview_image);
                        Glide
                                .with(myContext)
                                .load(currentPhotoPathName)
                                .into(previewImage);
                        nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                nagDialog.dismiss();
                            }
                        });
                        nagDialog.show();
                    }
                }, 300);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictureInfoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView displayImage;

        public ViewHolder(View itemView) {
            super(itemView);
            displayImage = (ImageView) itemView.findViewById(R.id.display_image);
        }
    }


    public int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
