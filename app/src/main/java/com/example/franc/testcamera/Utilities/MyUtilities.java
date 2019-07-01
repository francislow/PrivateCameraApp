package com.example.franc.testcamera.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.MotionEvent;
import android.widget.Button;

import com.example.franc.testcamera.ActivityMain;
import com.example.franc.testcamera.SQLiteDatabases.PicturesDatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
}
