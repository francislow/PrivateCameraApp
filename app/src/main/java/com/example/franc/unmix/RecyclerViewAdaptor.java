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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.unmix.Fragments.FragmentPage2;
import com.example.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;
import com.example.franc.unmix.Utilities.MyUtilities;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 27/6/2019.
 */

public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
    public ArrayList<String> categoryNames = new ArrayList<>();
    public ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
    private Context myContext;
    // associatedFragment = FragmentPage2
    private Fragment associatedFragment;
    private PicturesDatabaseHelper mydb;
    private boolean first_detection;


    public RecyclerViewAdaptor(Context context, ArrayList<String> categoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        this.myContext = context;
        this.categoryNames = categoryNames;
        this.photoPathLists = photoPathLists;

        associatedFragment = ActivityMain.swipeAdaptor.getItem(2);
        mydb = new PicturesDatabaseHelper(myContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_page2_item_layout, parent, false);
        return new ViewHolder(view);
    }
/*
    // This is called when adaptor is set to null in onPause() of fragment/ activity
    // Thus will be called when fragment is onPaused();
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.d(TAG, "onDetachedFromRecyclerView: ran");

        // Delete all pictures in database
        mydb.deleteAllRowsPTable();
    }


    // This runs after onDetachedFromRecyclerView
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d(TAG, "onViewDetachedFromWindow: " + holder.categoryTV.getText());

        // Delete all pictures in database under this holder category
        // (Just to be save since scrolling down will run on view detach)
        mydb.deletePicturesPTable(holder.categoryTV.getText().toString());

        // Save pictures properties into database
        GridLayout currentGridLayout = holder.gridLayout;
        for (int g = 0; g < currentGridLayout.getChildCount(); g++) {
            // Update database to save positions of pictures
            CustomPicture currentCP = (CustomPicture) currentGridLayout.getChildAt(g);
            mydb.insertNewRowPTable(currentCP.getPhotoPath(), holder.categoryTV.getText().toString(), currentCP.getLabelName(), null, null);
        }
    }
    */

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ran " + categoryNames.get(position));
        // Add category name
        holder.categoryTV.setText(categoryNames.get(position));
        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);

        // Add custom picture to gridlayout
        ArrayList<String> currentPhotoPathList = photoPathLists.get(position);
        holder.gridLayout.removeAllViews();
        for (final String currentPhotoPath : currentPhotoPathList) {
            CustomPicture newCustomPicture = new CustomPicture(myContext, currentPhotoPath);
            newCustomPicture.setCustomListener(holder.categoryTV, holder.line);
            holder.gridLayout.addView(newCustomPicture);

            // set on long click listener for custom picture (start drag)
            newCustomPicture.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ClipData data = ClipData.newPlainText("", "");
                    //View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(view);
                    view.startDrag(data, shadowBuilder, view, 0);
                    // Drag started
                    // Set constants after drag started
                    FragmentPage2.oldGridLayout = (GridLayout) view.getParent();
                    FragmentPage2.draggedPicture = (CustomPicture) view;

                    // Update recycler view
                    photoPathLists.get(holder.getAdapterPosition()).remove(((CustomPicture) view).getPhotoPath());
                    notifyItemChanged(holder.getAdapterPosition());
                    return true;
                }
            });
        }

        // Add the overall layout listeners
        holder.parentLayout.setOnDragListener(new View.OnDragListener() {
            @Override          // v -> parentlayout
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        first_detection = true;
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // if user did not drop in any on drag detection areas
                        if (!event.getResult() && first_detection) {
                            Log.d(TAG, "onDrag: No Drop Detected parent layout");
                            // Update recycler view
                            int oldPosition = categoryNames.indexOf(FragmentPage2.draggedPicture.getCatName());
                            photoPathLists.get(oldPosition).add(FragmentPage2.draggedPicture.getPhotoPath());
                            notifyItemChanged(oldPosition);
                        }
                        first_detection = false;
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                        holder.line.setVisibility(View.VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                        holder.line.setVisibility(View.INVISIBLE);
                        break;
                    case DragEvent.ACTION_DROP:
                        Log.d(TAG, "onDrag: Dropped at parent payout");
                        holder.categoryTV.setTypeface(Typeface.create(holder.categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                        holder.line.setVisibility(View.INVISIBLE);

                        // Update recycler view
                        photoPathLists.get(holder.getAdapterPosition()).add(FragmentPage2.draggedPicture.getPhotoPath());
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
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_down:
                                try {
                                    Log.d(TAG, "onMenuItemClick: Move down button pressed");
                                    // Update recycler view
                                    int oldIndex = holder.getAdapterPosition();
                                    Collections.swap(categoryNames, oldIndex, oldIndex + 1);
                                    Collections.swap(photoPathLists, oldIndex, oldIndex + 1);
                                    notifyItemRangeChanged(oldIndex, 2);

                                } catch (IndexOutOfBoundsException e) {
                                    Log.d(TAG, "onMenuItemClick: Caught IndexOutOfBoundError");
                                }
                                break;
                            case R.id.popup_up:
                                try {
                                    Log.d(TAG, "onMenuItemClick: Move up button pressed");
                                    // Update recycler view
                                    int oldIndex = holder.getAdapterPosition();
                                    Collections.swap(categoryNames, oldIndex, oldIndex - 1);
                                    Collections.swap(photoPathLists, oldIndex, oldIndex - 1);
                                    notifyItemRangeChanged(oldIndex - 1, 2);

                                } catch (IndexOutOfBoundsException e) {
                                    Log.d(TAG, "onMenuItemClick: Caught IndexOutOfBoundError");
                                }
                                break;
                            case R.id.popup_remove:
                                Log.d(TAG, "onMenuItemClick: remove button pressed");
                                // Prompts user if he really wants to delete all pictures permanently
                                final Dialog myDialog = new Dialog(myContext);
                                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                myDialog.setContentView(R.layout.dialog_delete_all_data);
                                myDialog.setCancelable(false);

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
                                        photoPathLists.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());

                                        // Refresh middle page
                                        ActivityMain.swipeAdaptor.getItem(1).onResume();

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
                                            notifyItemChanged(holder.getAdapterPosition());
                                            nagDialog.dismiss();
                                        } else {
                                            Toast.makeText(myContext, "Unable to edit label, you already have an exact label", Toast.LENGTH_LONG).show();
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
}
