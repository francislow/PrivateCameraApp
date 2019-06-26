package com.example.franc.testcamera.SQLiteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by franc on 19/6/2019.
 */

public class PicturesDatabaseHelper extends SQLiteOpenHelper {

    //database values
    private static final int versionNumber = 1;
    private static final String DATABASE_NAME = "pictures.db";

    //ID | getAbsolutePath() | label | year | month | day
    private static final String TABLE_NAME = "pictures_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "ABSPATH";
    private static final String COL_3 = "LABEL";
    private static final String COL_4 = "YEAR";
    private static final String COL_5 = "MONTH";
    private static final String COL_6 = "DAY";

    public PicturesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versionNumber);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //execSQL takes in a query to do whatever u want it to do
        //CREATE TABLE "table name", creates the table itself

        //Creates pictures table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ABSPATH TEXT, LABEL TEXT, " +
                "YEAR INTEGER, MONTH INTEGER, DAY INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String absPath, String label, String year, String month, String day) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, absPath);
        contentValues.put(COL_3, label);
        contentValues.put(COL_4, year);
        contentValues.put(COL_5, month);
        contentValues.put(COL_6, day);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateLabelData(int id, String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);

        contentValues.put(COL_3, label);
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

    public Integer deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {Integer.toString(id)});
    }
}