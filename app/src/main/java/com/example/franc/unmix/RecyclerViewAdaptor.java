package com.example.franc.unmix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
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
    private ArrayList<String> categoryNames = new ArrayList<>();
    private ArrayList<ArrayList<String>> photoPathLists = new ArrayList<>();
    private Context myContext;
    private Fragment associatedFragment;
    private PicturesDatabaseHelper mydb;


    public RecyclerViewAdaptor(Context context, ArrayList<String> categoryNames, ArrayList<ArrayList<String>> photoPathLists) {
        this.categoryNames = categoryNames;
        this.photoPathLists = photoPathLists;
        this.myContext = context;

        associatedFragment = ActivityMain.swipeAdaptor.getItem(2);
        mydb = new PicturesDatabaseHelper(myContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_page2_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // This is called when adaptor is set to null in onPause() of fragment/ activity
    // Thus will be called when fragment is onPaused();
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.d(TAG, "onDetachedFromRecyclerView: ran");

        // Update pictures
        mydb.deleteAllRowsPTable();
        for (int i = 0; i < getItemCount(); i++) {
            ViewHolder currentViewHolder = (ViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
            GridLayout currentGridLayout = currentViewHolder.gridLayout;
            for (int g = 0; g < currentGridLayout.getChildCount(); g++) {
                // Update database to save positions of pictures
                CustomPicture currentCP = (CustomPicture) currentGridLayout.getChildAt(g);
                mydb.insertNewRowPTable(currentCP.getPhotoPath(), currentViewHolder.categoryTV.getText().toString(), currentCP.getLabelName(), null, null);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set label names
        holder.categoryTV.setText(categoryNames.get(position));

        // Set grid view pictures
        ArrayList<String> currentPhotoPathList = photoPathLists.get(position);

        for (final String currentPhotoPath : currentPhotoPathList) {
            CustomPicture newCustomPicture = new CustomPicture(myContext, currentPhotoPath);
            newCustomPicture.setCustomListener(holder.categoryTV, holder.line);
            holder.gridLayout.addView(newCustomPicture);
        }
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

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parent_layout);
            categoryTV = (TextView) itemView.findViewById(R.id.cat_name);
            categoryTV.setTypeface(Typeface.create(categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);

            gridLayout = (GridLayout) itemView.findViewById(R.id.grid1);
            line = (ImageView) itemView.findViewById(R.id.line);

            parentLayout.setOnDragListener(new View.OnDragListener() {
                @Override          // v -> parentlayout
                public boolean onDrag(View v, DragEvent event) {
                    GridLayout newGridView = (GridLayout) ((LinearLayout) v).getChildAt(1);

                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            break;
                        case DragEvent.ACTION_DRAG_ENDED:
                            // if user did not drop in any on drag detection areas
                            if (!event.getResult()) {
                                System.out.println("no drop detected");
                                // This is freakin weird, why would dragged pic have a parent only when its the oni child
                                if (FragmentPage2.draggedPicture.getParent() != null) {
                                    ((GridLayout) FragmentPage2.draggedPicture.getParent()).removeView(FragmentPage2.draggedPicture);
                                }
                                FragmentPage2.oldGridLayout.addView(FragmentPage2.draggedPicture);
                            } else {
                                System.out.println("drop detected");
                            }
                            break;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            categoryTV.setTypeface(Typeface.create(categoryTV.getTypeface(), Typeface.NORMAL), Typeface.NORMAL);
                            line.setVisibility(View.VISIBLE);
                            break;
                        case DragEvent.ACTION_DRAG_EXITED:
                            categoryTV.setTypeface(Typeface.create(categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                            line.setVisibility(View.INVISIBLE);
                            break;
                        case DragEvent.ACTION_DROP:
                            categoryTV.setTypeface(Typeface.create(categoryTV.getTypeface(), Typeface.BOLD), Typeface.BOLD);
                            line.setVisibility(View.INVISIBLE);

                            newGridView.addView(FragmentPage2.draggedPicture);
                            boolean hasInsertedData = mydb.updateCategoryNamePTable((String) FragmentPage2.draggedPicture.getPhotoPath(), categoryTV.getText().toString());
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

            categoryTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(myContext, categoryTV);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.popup_down:
                                    try {
                                        int oldIndex = categoryNames.indexOf(categoryTV.getText().toString());
                                        Collections.swap(categoryNames, oldIndex, oldIndex + 1);
                                        boolean bool = mydb.deleteAllRowsCTable();
                                        if (bool) {
                                            Toast.makeText(myContext, "Successfully deleted all cat name", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(myContext, "Error deleted all cat name", Toast.LENGTH_SHORT).show();
                                        }
                                        for (String catName : categoryNames) {
                                            mydb.insertNewRowCTable(catName);
                                        }
                                        associatedFragment.onResume();

                                    } catch (IndexOutOfBoundsException e) {
                                    }
                                    break;
                                case R.id.popup_up:
                                    try {
                                        int oldIndex = categoryNames.indexOf(categoryTV.getText().toString());
                                        Collections.swap(categoryNames, oldIndex, oldIndex - 1);
                                        boolean bool = mydb.deleteAllRowsCTable();
                                        if (bool) {
                                            Toast.makeText(myContext, "Successfully deleted all cat name", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(myContext, "Error deleted all cat name", Toast.LENGTH_SHORT).show();
                                        }
                                        for (String catName : categoryNames) {
                                            mydb.insertNewRowCTable(catName);
                                        }
                                        associatedFragment.onResume();

                                    } catch (IndexOutOfBoundsException e) {
                                    }
                                    break;
                                case R.id.popup_remove:
                                    // Prompts user if he really wants to delete all pictures permanently
                                    final Dialog myDialog = new Dialog(myContext);
                                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    myDialog.setContentView(R.layout.dialog_delete_all_data);
                                    myDialog.setCancelable(false);

                                    Button noButton = (Button) myDialog.findViewById(R.id.no_button);
                                    noButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            myDialog.dismiss();
                                        }
                                    });

                                    Button yesButton = (Button) myDialog.findViewById(R.id.yes_button);
                                    yesButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean hasDeletedCatNameData = mydb.deleteRowCTable(categoryTV.getText().toString());
                                            if (hasDeletedCatNameData) {
                                                Toast.makeText(myContext, "Successfully deleted cat name", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(myContext, "Error deleted cat name", Toast.LENGTH_SHORT).show();
                                            }
                                            parentLayout.removeAllViews();
                                            gridLayout.removeAllViews();

                                            ActivityMain.swipeAdaptor.getItem(2).onPause();
                                            ActivityMain.swipeAdaptor.getItem(2).onResume();
                                            // Refresh middle page
                                            ActivityMain.swipeAdaptor.getItem(1).onResume();

                                            myDialog.dismiss();
                                        }
                                    });
                                    myDialog.show();
                                    break;
                                case R.id.popup_edit:
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
                                            String oldCategoryName = categoryTV.getText().toString().trim();
                                            if (!MyUtilities.hasDuplicatedCatNamesInCTable(newCategoryName, myContext)) {
                                                boolean updated = mydb.updateCategoryNameDataCTable(oldCategoryName, newCategoryName);
                                                boolean updated2 = mydb.updateAllCategoryNamePTable(oldCategoryName, newCategoryName);
                                                if (updated && updated2) {
                                                    categoryTV.setText(newCategoryName);
                                                    Toast.makeText(myContext, "successfully updated cat name and pic cat name", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(myContext, "Error updating cat name and pic cat name", Toast.LENGTH_LONG).show();
                                                }
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
    }
}
