package com.example.franc.unmix.SQLiteDatabases;

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

    //ID | getAbsolutePath() | categoryName | label | month | day
    private static final String PICTURES_TABLE_NAME = "pictures_table";
    private static final String COL_P_1 = "ID";
    private static final String COL_P_2 = "ABSPATH";
    private static final String COL_P_3 = "CATEGORYNAME";
    private static final String COL_P_4 = "LABEL";
    private static final String COL_P_5 = "MONTH";
    private static final String COL_P_6 = "DAY";

    //ID | categoryName
    private static final String CATEGORIES_TABLE_NAME = "category_table";
    private static final String COL_C_1 = "ID";
    private static final String COL_C_2 = "CATEGORYNAME";


    public PicturesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versionNumber);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creates pictures table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PICTURES_TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, ABSPATH TEXT, CATEGORYNAME TEXT, " +
                "LABEL TEXT, MONTH INTEGER, DAY INTEGER);");

        //Creates category table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CATEGORIES_TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORYNAME TEXT, POSITION INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
        onCreate(db);
    }

    /* Pictures Table ----------------------------------------------------------------------------*/
    public boolean insertNewRowPTable(String absPath, String categoryName, String label, String month, String day) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_P_2, absPath);
        contentValues.put(COL_P_3, categoryName);
        contentValues.put(COL_P_4, label);
        contentValues.put(COL_P_5, month);
        contentValues.put(COL_P_6, day);

        long result = db.insert(PICTURES_TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateCategoryNamePTable(String pathName, String newCategoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_P_2, pathName);

        contentValues.put(COL_P_3, newCategoryName);
        long result = db.update(PICTURES_TABLE_NAME, contentValues, "ABSPATH = ?", new String[] {pathName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateLabelNamePTable(String pathName, String newLabelName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_P_2, pathName);

        contentValues.put(COL_P_4, newLabelName);
        long result = db.update(PICTURES_TABLE_NAME, contentValues, "ABSPATH = ?", new String[] {pathName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateAllCategoryNamePTable(String categoryName, String newCategoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_P_3, newCategoryName);

        long result = db.update(PICTURES_TABLE_NAME, contentValues, "CATEGORYNAME = ?", new String[] {categoryName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getAllDataPTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + PICTURES_TABLE_NAME, null);
        return res;
    }

    public boolean deleteRowPTable(String pathName) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(PICTURES_TABLE_NAME, "ABSPATH = ?", new String[] {pathName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getLabelFromPathPTable(String pathName) {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + PICTURES_TABLE_NAME, new String[] {pathName});
        Cursor res = db.query(PICTURES_TABLE_NAME, null, "ABSPATH = ?", new String[] {pathName}, null, null, null);
        return res;
    }

    /* Categories Table ----------------------------------------------------------------------------*/
    public boolean insertNewRowCTable (String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_C_2, categoryName);

        long result = db.insert(CATEGORIES_TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean updateCategoryNameDataCTable(String categoryName, String newCategoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_C_2, newCategoryName);
        long result = db.update(CATEGORIES_TABLE_NAME, contentValues, "CATEGORYNAME = ?", new String[] {categoryName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public Cursor getAllDataCTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CATEGORIES_TABLE_NAME, null);
        return res;
    }

    public boolean deleteRowCTable(String catName) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(CATEGORIES_TABLE_NAME, "CATEGORYNAME = ?", new String[] {catName});

        if (result == -1) {
            return false;
        }
        return true;
    }

    public boolean deleteAllRowsCTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(CATEGORIES_TABLE_NAME, null, null);

        if (result == -1) {
            return false;
        }
        return true;
    }
}