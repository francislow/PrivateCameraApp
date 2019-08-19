package com.chalkboystudios.franc.unmix.Utilities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chalkboystudios.franc.unmix.ActivityMain;
import com.chalkboystudios.franc.unmix.R;
import com.chalkboystudios.franc.unmix.SQLiteDatabases.PicturesDatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by franc on 24/6/2019.
 */

public class MyUtilities {

    //Checks if the there category table in database already contains a specific category name
    public static boolean hasDuplicatedCatNamesInCTable(String categoryNameToCheck, Context myContext) {
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(myContext);
        Cursor res = mydb.getAllDataCTable();
        while (res.moveToNext()) {
            if (res.getString(1).equals(categoryNameToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasDuplicatedCatNames(String categoryNameToCheck, ArrayList<String> categoryNames) {
        for (String categoryName : categoryNames) {
            if (categoryName.equals(categoryNameToCheck)) {
                return true;
            }
        }
        return false;
    }

    // Prints out picture database
    public static void printOutPTable(Context myContext) {
        System.out.println("ran print out table");
        Log.d(TAG, "printOutPTable: asdasdas ran");
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(myContext);
        Cursor res = mydb.getAllDataPTable();
        while (res.moveToNext()) {
            Log.d(TAG, "printOutPTable: " +
                    res.getString(0) + " " +
                    res.getString(1) + " " +
                    res.getString(2) + " " +
                    res.getString(3) + " " +
                    res.getString(4) + " " +
                    res.getString(5) + " " +
                    res.getString(6) + " " +
                    res.getString(7) + " " +
                    res.getString(8) + " " +
                    res.getString(9) + " " +
                    res.getString(10) + "\n");
        }
    }

    // Prints out picture database
    public static void printOutCTable(Context myContext) {
        PicturesDatabaseHelper mydb = new PicturesDatabaseHelper(myContext);
        Cursor res = mydb.getAllDataCTable();
        while (res.moveToNext()) {
            System.out.print(res.getString(0) + " ");
            System.out.print(res.getString(1) + "\n");
        }
    }

    //If user touched down and up a button within button space
    public static boolean touchUpInButton(MotionEvent motionEvent, Button button) {
        int[] buttonPosition = new int[2];
        button.getLocationOnScreen(buttonPosition);

        if (motionEvent.getRawX() >= buttonPosition[0] && motionEvent.getRawX() <= (buttonPosition[0] + button.getWidth())) {
            if (motionEvent.getRawY() >= buttonPosition[1] && motionEvent.getRawY() <= (buttonPosition[1] + button.getHeight())) {
                return true;
            }
        }
        return false;
    }

    // Makes a copy of the image file selected and put inside app folder
    public static Uri copyMediaStoreUriToCacheDir(Uri uri, String filename, Context myContext) {
        String destinationFilename = myContext.getExternalFilesDir(ActivityMain.APPIMAGEFOLDERNAME) + "/" + filename + ".jpg";
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(myContext.getContentResolver().openInputStream(uri));
            //bis = new BufferedInputStream(App.getAppContext().getContentResolver().openInputStream(uri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);

            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);

            return Uri.fromFile(new File(destinationFilename));
        } catch (IOException e) {
            //
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                System.out.println("copyMediaStoreUriToCacheDir ran into IOException shucks..");
            }
        }
        return null;
    }

    public static void createOneTimeIntroDialog(Context context, String prefKey, int drawableId) {
        Log.d(TAG, "createOneTimeIntroDialog: tried running one time dialog");
        SharedPreferences prefs = context.getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE);
        boolean first_time_flag = prefs.getBoolean(prefKey, true);//"No name defined" is the default value.

        if (first_time_flag) {
            // if no entry add first time flag  = false as entry
            SharedPreferences.Editor editor = context.getSharedPreferences(ActivityMain.MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(prefKey, false);
            editor.apply();

            // if first time starting app, apply dialog
            final Dialog myDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            // Set Layout
            myDialog.setContentView(R.layout.dialog_information);
            ImageView informationIV = myDialog.findViewById(R.id.informationIV);
            informationIV.setBackground(context.getResources().getDrawable(drawableId));

            // Set dialog background to transparent
            myDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

            // Set appear and disappear transitions
            myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFade;

            // Set cancel on back button pressed
            myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });

            Button cancelDialogButton = (Button) myDialog.findViewById(R.id.cancel_dialog_button);
            cancelDialogButton.setVisibility(View.VISIBLE);
            cancelDialogButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1f);
                    PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1f);
                    PropertyValuesHolder alphaUp = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1f);

                    PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.7f);
                    PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.7f);
                    PropertyValuesHolder alphaDown = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0.5f);
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaDown, scaleXDown, scaleYDown).start();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();
                            break;
                        case MotionEvent.ACTION_UP:
                            ObjectAnimator.ofPropertyValuesHolder(view, alphaUp, scaleXUp, scaleYUp).start();

                            //If user's touch up is still inside button
                            if (MyUtilities.touchUpInButton(motionEvent, (Button) view)) {
                                myDialog.dismiss();
                            }
                    }
                    return true;
                }
            });
            myDialog.show();
        }
    }

    public static void deleteFile(String pathName) {
        File file = new File(pathName);
        if (file.exists()) {
            file.delete();
        }
    }
}