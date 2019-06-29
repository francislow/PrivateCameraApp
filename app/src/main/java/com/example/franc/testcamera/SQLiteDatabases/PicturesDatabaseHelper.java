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

    //ID | getAbsolutePath() | categoryName | year | month | day
    private static final String TABLE_NAME = "pictures_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "ABSPATH";
    private static final String COL_3 = "CATEGORYNAME";
    private static final String COL_4 = "YEAR";
    private static final String COL_5 = "MONTH";
    private static final String COL_6 = "DAY";

    //ID | categoryName
    private static final String TABLE_NAME2 = "category_table";
    private static final String COL2_1 = "ID";
    private static final String COL2_2 = "CATEGORYNAME";

    public PicturesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versionNumber);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //execSQL takes in a query to do whatever u want it to do
        //CREATE TABLE "table name", creates the table itself

        //Creates pictures table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ABSPATH TEXT, CATEGORYNAME TEXT, " +
                "YEAR INTEGER, MONTH INTEGER, DAY INTEGER);");

        //Creates category table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME2 +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORYNAME TEXT);");
        //insert Unsorted into categoryName first
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(db);
    }

    public boolean insertData(String absPath, String categoryName, String year, String month, String day) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, absPath);
        contentValues.put(COL_3, categoryName);
        contentValues.put(COL_4, year);
        contentValues.put(COL_5, month);
        contentValues.put(COL_6, day);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean insertCategoryNameData(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2_2, categoryName);

        long result = db.insert(TABLE_NAME2, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateCategoryNameData(String pathName, String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, pathName);

        contentValues.put(COL_3, categoryName);
        long result = db.update(TABLE_NAME, contentValues, "ABSPATH = ?", new String[] {pathName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getCategoryNameData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME2, null);
        return res;
    }


    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getUniqueCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct " + COL_3 + " from " + TABLE_NAME, null);
        return res;
    }

    public Integer deleteData(String pathName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ABSPATH = ?", new String[] {pathName});
    }
}