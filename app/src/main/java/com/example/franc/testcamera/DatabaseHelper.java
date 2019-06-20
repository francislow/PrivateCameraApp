package com.example.franc.testcamera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by franc on 19/6/2019.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    //database values
    private static final int versionNumber = 1;

    private static final String DATABASE_NAME      = "notes.db";
    private static final String TABLE_NAME      = "notes_table";

    private static final String COL_1      = "ID";
    private static final String COL_2      = "TITLE";
    private static final String COL_3      = "DES";
    private static final String COL_4      = "LMARGIN";
    private static final String COL_5      = "RMARGIN";
    private static final String COL_6      = "TMARGIN";
    private static final String COL_7      = "BMARGIN";
    private static final String COL_8      = "WIDTH";
    private static final String COL_9      = "HEIGHT";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versionNumber);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //execSQL takes in a query to do whatever u want it to do
        //CREATE TABLE "table name", creates the table itself
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DES TEXT, " +
                "LMARGIN INTEGER, RMARGIN INTEGER, TMARGIN INTEGER, BMARGIN INTEGER, WIDTH INTEGER, HEIGHT INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String title, String des, int leftMargin, int rightMargin,
                              int topMargin, int btmMargin, int width, int height) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, des);
        contentValues.put(COL_4, leftMargin);
        contentValues.put(COL_5, rightMargin);
        contentValues.put(COL_6, topMargin);
        contentValues.put(COL_7, btmMargin);
        contentValues.put(COL_8, width);
        contentValues.put(COL_9, height);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updatePositionData(int id, int leftMargin, int rightMargin, int topMargin, int btmMargin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);

        contentValues.put(COL_4, leftMargin);
        contentValues.put(COL_5, rightMargin);
        contentValues.put(COL_6, topMargin);
        contentValues.put(COL_7, btmMargin);
        long result = db.update(TABLE_NAME, contentValues, "id = ?", new String[] {Integer.toString(id)});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateSizeData(int id, int width, int height) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);

        contentValues.put(COL_8, width);
        contentValues.put(COL_9, height);
        long result = db.update(TABLE_NAME, contentValues, "id = ?", new String[] {Integer.toString(id)});

        if (result == -1) {
            return false;
        }
        return true;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}
