package com.example.franc.unmix;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.franc.unmix.Fragments.FragmentPage2;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyAnimUtilities;
import com.example.franc.unmix.Utilities.MyUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 27/6/2019.
 */

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
    public ArrayList<String> categoryNames = new ArrayList<>();
    public ArrayList<ArrayList<CustomPicture>> customPicsLists = new ArrayList<>();
    private Context myContext;
    // associatedFragment = FragmentPage2
    private Fragment associatedFragment;
    private PicturesDatabaseHelper mydb;
    private boolean applied_initial_customisation_flag = false;


    public RecyclerViewAdaptor(Context context, ArrayList<String> categoryNames, ArrayList<ArrayList<CustomPicture>> customPicsLists) {
        this.myContext = context;
        this.categoryNames = categoryNames;
        this.customPicsLists = customPicsLists;

        associatedFragment = ActivityMain.swipeAdaptor.getItem(1);
        mydb = new PicturesDatabaseHelper(myContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_page2_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.d(TAG, "onDetachedFromRecyclerView: ran");

        // Update database
        mydb.deleteAllRowsCTable();
        for (String categoryName : categoryNames) {
            mydb.insertNewRowCTable(categoryName);
        }
        mydb.deleteAllRowsPTable();
        for (int i = 0; i < customPicsLists.size(); i++) {
            ArrayList<CustomPicture> customPictureList = customPicsLists.get(i);

            String categoryName = categoryNames.get(customPicsLists.indexOf(customPictureList));
            for (int j = 0; j < customPictureList.size(); j++) {
                CustomPicture currentPic = customPictureList.get(j);
                String photoPathName = currentPic.getPhotoPath();
                String labelName = currentPic.getLabelName();
                int year = currentPic.getYear();
                int month = currentPic.getMonth();
                int day = currentPic.getDay();
                int hour = currentPic.getHour();
                int min = currentPic.getMin();
                int sec = currentPic.getSec();
                mydb.insertNewRowPTable(photoPathName, categoryName, labelName, null,
                        year, month, day, hour, min, sec);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ran " + categoryNames.get(position));
        // Add category name
        final String currentCategoryName = categoryNames.get(holder.getAdapterPosition());
        
        /*// apply initial customisation on unsorted
        if (currentCategoryName.equals(ActivityMain.DEFAULTCATEGORYNAME)  && !applied_initial_customisation_flag) {
            Log.d(TAG, "onBindViewHolder: IT HAPENNNNNNNNNNNNEDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
            holder.catOption.setVisibility(View.GONE);
            //applied_initial_customisation_flag = true;
        }*/

        // Set category name
        holder.categoryTV.setText(currentCategoryName);
        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);

        // Set custom pictures to gridlayout
        final ArrayList<CustomPicture> currentCustomPicList = customPicsLists.get(position);
        holder.gridLayout.removeAllViews();
        for (int i = 0; i < currentCustomPicList.size(); i++) {
            // get properties for current picture
            final CustomPicture currentPic = currentCustomPicList.get(i);
            final String currentPhotoPath = currentPic.getPhotoPath();
            final String currentLabelName = currentPic.getLabelName();

           if ((GridLayout) currentPic.getParent() != null) {
                ((GridLayout) currentPic.getParent()).removeView(currentPic);
            }
            holder.gridLayout.addView(currentPic);

            // Set Label Name for each pic
            currentPic.getLabelNameTVN().setText(currentPic.getLabelName());

            // Set white space opacity (whether label name is present or not)
            if (!currentPic.getLabelName().equals("")) {
                // if there is label name
                currentPic.getWhiteSpace().getBackground().setAlpha(190);
            } else {
                // if there is no label name
                currentPic.getWhiteSpace().getBackground().setAlpha(120);
            }

            // Set black indicator to opacity 0 by default
            currentPic.getBlackSpace().setAlpha(0);
            currentPic.getBlackSpace2().setAlpha(0);

            // Set on long click listener for custom picture (start drag)
            currentPic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MyUtilities.printOutPTable(myContext);
                    Log.d(TAG, "onLongClick: on dragged picture ran");

                    ClipData data = ClipData.newPlainText("", "");
                    //View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
                    // Set constants after drag started
                    FragmentPage2.oldGridLayout = (GridLayout) view.getParent();
                    CustomPicture newRefCurrentPic = new CustomPicture(myContext, currentPic.getPhotoPath(), currentPic.getLabelName(), currentPic.getCatName(), null, currentPic.getYear(), currentPic.getMonth(), currentPic.getDay(), currentPic.getHour(), currentPic.getMin(), currentPic.getSec());
                    FragmentPage2.draggedPicture = newRefCurrentPic;
                    FragmentPage2.oldGridPosition = customPicsLists.get(holder.getAdapterPosition()).indexOf(((CustomPicture) view));

                    // Update recycler view
                    customPicsLists.get(holder.getAdapterPosition()).remove(((CustomPicture) view));
                    // For some reason notify change removes drop detection for the holder
                    //holder.gridLayout.removeView(newCustomPicture);
                    notifyItemChanged(holder.getAdapterPosition());
                    // this is to solve the drop within milli seconds error
                    currentPic.setVisibility(View.GONE);
                    // Drag started
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                }
            });

            currentPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation scaleDown = new ScaleAnimation(1.f, 0.5f, 1, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    Animation fadeout = new AlphaAnimation(1.f, 0.f);
                    scaleDown.setDuration(100);
                    fadeout.setDuration(100);
                    AnimationSet combinedAnim = new AnimationSet(true);
                    combinedAnim.addAnimation(scaleDown);
                    combinedAnim.addAnimation(fadeout);
                    currentPic.startAnimation(combinedAnim);
                    currentPic.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Show preview image function
                            final Dialog nagDialog = new Dialog(myContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                            nagDialog.setContentView(R.layout.dialog_preview_image);

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
                    }, 100);
                }
            });

            // set drag listeners for custom pictures
            currentPic.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    // v -> each custom picture
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            if (event.getResult()) {
                                Log.d(TAG, "onDrag: Drop detected at custom picture");
                            } else {
                                Log.d(TAG, "onDrag: No drop detected at custom picture");
                                int oldCatPosition = categoryNames.indexOf(FragmentPage2.draggedPicture.getCatName());

                                // To make sure only either the current pic or the holder adds back the dragged picture
                                if (!customPicsLists.get(oldCatPosition).contains(FragmentPage2.draggedPicture)) {
                                    // Update recycler view
                                    customPicsLists.get(oldCatPosition).add(FragmentPage2.oldGridPosition, FragmentPage2.draggedPicture);
                                    notifyItemChanged(oldCatPosition);
                                }
                            }
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            Log.d(TAG, "onDrag: entered custom picture");
                            // set spacing indicators
                            // automatically remove blackspace2 for the last child
                            ((CustomPicture) holder.gridLayout.getChildAt(holder.gridLayout.getChildCount() - 1)).getBlackSpace2().setAlpha(0);

                            currentPic.getBlackSpace().setAlpha(1);
                            if (currentCustomPicList.indexOf(currentPic) != 0) {
                                ((CustomPicture) holder.gridLayout.getChildAt(currentCustomPicList.indexOf(currentPic) - 1)).getBlackSpace2().setAlpha(1);
                            }
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            Log.d(TAG, "onDrag: exited custom picture");
                            // set spacing indicators
                            // automatically add blackspace2 for the last child
                            ((CustomPicture) holder.gridLayout.getChildAt(holder.gridLayout.getChildCount() - 1)).getBlackSpace2().setAlpha(1);

                            currentPic.getBlackSpace().setAlpha(0);
                            if (currentCustomPicList.indexOf(currentPic) != 0) {
                                ((CustomPicture) holder.gridLayout.getChildAt(currentCustomPicList.indexOf(currentPic) - 1)).getBlackSpace2().setAlpha(0);
                            }
                            break;
                        /* User dropped into one of the custom picture */
                        case DragEvent.ACTION_DROP:
                            Log.d(TAG, "onDrag: Dropped at custompicture");
                            holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                            holder.line.setVisibility(View.INVISIBLE);

                            // Update recycler view
                            int currentChildIndex = customPicsLists.get(categoryNames.indexOf(currentPic.getCatName())).indexOf(currentPic);
                            customPicsLists.get(holder.getAdapterPosition()).add(currentChildIndex, FragmentPage2.draggedPicture);
                            FragmentPage2.draggedPicture.setCategoryName(currentPic.getCatName());
                            notifyItemChanged(holder.getAdapterPosition());
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            currentPic.getLabelNameTVN().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // animation for picture blink effect when click
                    Animation fadeout = new AlphaAnimation(1.f, 0.f);
                    fadeout.setDuration(200);
                    currentPic.getLabelNameTVN().startAnimation(fadeout);
                    currentPic.getWhiteSpace().startAnimation(fadeout);
                    currentPic.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            /* Listener for label name textview */
                            // Edit label function
                            final Dialog nagDialog = new Dialog(myContext);
                            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            nagDialog.setContentView(R.layout.dialog_edit_label_name);
                            nagDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

                            final EditText labelNameET = (EditText) nagDialog.findViewById(R.id.editT4);
                            labelNameET.setText(currentLabelName);
                            labelNameET.setSelection(currentLabelName.length());
                            // Automatically bring up keyboard
                            labelNameET.requestFocus();
                            nagDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                            //Set add category button on click listener
                            Button submitButton = (Button) nagDialog.findViewById(R.id.button4);
                            submitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newLabelName = labelNameET.getText().toString().trim();
                                    nagDialog.dismiss();

                                    // Update recycler view
                                    currentPic.setLabelName(newLabelName);
                                    notifyItemChanged(customPicsLists.indexOf(currentCustomPicList));

                                    MyUtilities.createOneTimeIntroDialog(myContext, "first_time_page3", R.drawable.starting_dialog3);
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
                    }, 100);
                }
            });
        }

        // Add the overall layout listeners
        holder.parentLayout.setOnDragListener(new View.OnDragListener() {
            @Override          // v -> parentlayout
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // if user did not drop in any on drag detection areas
                        if (!event.getResult()) {
                            Log.d(TAG, "onDrag: No Drop Detected parent layout");
                            int oldCatPosition = categoryNames.indexOf(FragmentPage2.draggedPicture.getCatName());

                            // To make sure only either the current pic or the holder adds back the dragged picture
                            if (!customPicsLists.get(oldCatPosition).contains(FragmentPage2.draggedPicture)) {
                                // Update recycler view
                                customPicsLists.get(oldCatPosition).add(FragmentPage2.oldGridPosition, FragmentPage2.draggedPicture);
                                notifyItemChanged(oldCatPosition);
                            }
                        } else if (event.getResult()) {
                            MyUtilities.createOneTimeIntroDialog(myContext, "first_time_page5", R.drawable.starting_dialog5);
                        }

                        // set back to normal padding space
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(holder.line.getLayoutParams());
                        lp3.topMargin = convertDpToPx(myContext, 15);
                        holder.line.setLayoutParams(lp3);
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(TAG, "onDrag: Entered Parent Layout");
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                        holder.line.setVisibility(View.VISIBLE);

                        // set spacing indicators
                        try {
                            ((CustomPicture) holder.gridLayout.getChildAt(holder.gridLayout.getChildCount() - 1)).getBlackSpace2().setAlpha(1);
                        } catch (Exception e) {

                        }

                        // set padding space increase
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(holder.line.getLayoutParams());
                        lp.topMargin = convertDpToPx(myContext, 30);
                        holder.line.setLayoutParams(lp);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(TAG, "onDrag: Exited Parent Layout");
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                        holder.line.setVisibility(View.INVISIBLE);

                        // set spacing indicators
                        try {
                            ((CustomPicture) holder.gridLayout.getChildAt(holder.gridLayout.getChildCount() - 1)).getBlackSpace2().setAlpha(0);
                        } catch (Exception e) {
                        }

                        // set back to normal padding space
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(holder.line.getLayoutParams());
                        lp1.topMargin = convertDpToPx(myContext, 15);
                        holder.line.setLayoutParams(lp1);
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "onDrag: Dropped at parent payout");
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                        holder.line.setVisibility(View.INVISIBLE);

                        // Update recycler view
                        customPicsLists.get(holder.getAdapterPosition()).add(FragmentPage2.draggedPicture);
                        FragmentPage2.draggedPicture.setCategoryName(holder.categoryTV.getText().toString());
                        notifyItemChanged(holder.getAdapterPosition());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        // Add category option listener
        holder.catOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: category option pressed");
                final PopupMenu popupMenu = new PopupMenu(myContext, holder.catOption);
                final int currentIndex = holder.getAdapterPosition();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_down:
                                try {
                                    Log.d(TAG, "onMenuItemClick: Move down button pressed");
                                    // Update recycler view
                                    Collections.swap(categoryNames, currentIndex, currentIndex + 1);
                                    Collections.swap(customPicsLists, currentIndex, currentIndex + 1);
                                    notifyItemRangeChanged(currentIndex, 2);

                                } catch (IndexOutOfBoundsException e) {
                                    Log.d(TAG, "onMenuItemClick: Caught IndexOutOfBoundError");
                                }
                                break;
                            case R.id.popup_up:
                                try {
                                    Log.d(TAG, "onMenuItemClick: Move up button pressed");
                                    // Update recycler view
                                    Collections.swap(categoryNames, currentIndex, currentIndex - 1);
                                    Collections.swap(customPicsLists, currentIndex, currentIndex - 1);
                                    notifyItemRangeChanged(currentIndex - 1, 2);

                                } catch (IndexOutOfBoundsException e) {
                                    Log.d(TAG, "onMenuItemClick: Caught IndexOutOfBoundError");
                                }
                                break;
                            case R.id.popup_remove:
                                Log.d(TAG, "onMenuItemClick: remove button pressed");
                                // Prompts user if he really wants to delete all pictures permanently
                                final Dialog myDialog = new Dialog(myContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                                myDialog.setContentView(R.layout.dialog_delete_all_data);
                                myDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
                                myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFade;
                                myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        dialog.dismiss();
                                    }
                                });

                                // User pressed "No"
                                Button noButton = (Button) myDialog.findViewById(R.id.no_button);
                                noButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        myDialog.dismiss();
                                    }
                                });

                                // User pressed "Yes"
                                Button yesButton = (Button) myDialog.findViewById(R.id.yes_button);
                                yesButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // Update recycler view
                                        categoryNames.remove(holder.getAdapterPosition());
                                        customPicsLists.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());

                                        myDialog.dismiss();
                                    }
                                });
                                myDialog.show();
                                break;
                            case R.id.popup_edit:
                                Log.d(TAG, "onMenuItemClick: edit button pressed");
                                final Dialog nagDialog = new Dialog(myContext);
                                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                nagDialog.setContentView(R.layout.dialog_edit_cat_name);
                                nagDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

                                EditText catNameET = (EditText) nagDialog.findViewById(R.id.editT2);
                                catNameET.setText(currentCategoryName);
                                // Update recycler view
                                for (CustomPicture pic : customPicsLists.get(currentIndex)) {
                                    pic.setCategoryName(currentCategoryName);
                                }

                                catNameET.setSelection(currentCategoryName.length());
                                // Automatically bring up keyboard
                                catNameET.requestFocus();
                                nagDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                                //Set add category button on click listener
                                Button submitButton = (Button) nagDialog.findViewById(R.id.button2);
                                submitButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText categoryNameET = (EditText) nagDialog.findViewById(R.id.editT2);
                                        String newCategoryName = categoryNameET.getText().toString().trim();
                                        if (!MyUtilities.hasDuplicatedCatNames(newCategoryName, categoryNames)) {
                                            // Update recycler view
                                            categoryNames.set(holder.getAdapterPosition(), newCategoryName);
                                            /*// Update cat name for all pic in holder
                                            for () {

                                            }*/

                                            notifyItemChanged(holder.getAdapterPosition());
                                            nagDialog.dismiss();
                                        } else {
                                            Toast.makeText(myContext, "Unable to edit label, you already have an exact label name", Toast.LENGTH_LONG).show();
                                        }
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
                        return true;
                    }
                });
                popupMenu.inflate(R.menu.pop_menu);
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout parentLayout;
        TextView categoryTV;
        GridLayout gridLayout;
        ImageView line;
        ImageView catOption;

        public ViewHolder(View itemView) {
            super(itemView);
            catOption = (ImageView) itemView.findViewById(R.id.cat_option);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
            categoryTV = (TextView) itemView.findViewById(R.id.cat_name);
            gridLayout = (GridLayout) itemView.findViewById(R.id.grid1);
            line = (ImageView) itemView.findViewById(R.id.line);
        }
        
    /*    public void setGoneCatOption() {
            catOption.setVisibility(View.GONE);
        }*/
    }

    private class MyDragShadowBuilder extends View.DragShadowBuilder {
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

    public int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
