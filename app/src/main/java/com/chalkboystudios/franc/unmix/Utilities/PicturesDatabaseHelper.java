package com.chalkboystudios.franc.unmix.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database helper
 */
public class PicturesDatabaseHelper extends SQLiteOpenHelper {

    // Database details
    private static final int versionNumber = 3;
    private static final String DATABASE_NAME = "pictures.db";

    // Picture Table format:
    // ID | path | categoryName | label | position | year | month | day | hour | minutes | seconds
    private static final String PICTURES_TABLE_NAME = "pictures_table";
    private static final String COL_P_1 = "ID";
    private static final String COL_P_2 = "ABSPATH";
    private static final String COL_P_3 = "CATEGORYNAME";
    private static final String COL_P_4 = "LABEL";
    private static final String COL_P_5 = "POSITION";
    private static final String COL_P_6 = "YEAR";
    private static final String COL_P_7 = "MONTH";
    private static final String COL_P_8 = "DAY";
    private static final String COL_P_9 = "HOUR";
    private static final String COL_P_10 = "MIN";
    private static final String COL_P_11 = "SEC";

    // Category Table format:
    // ID | categoryName
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
                "LABEL TEXT, POSITION INTEGER, YEAR INTEGER, MONTH INTEGER, DAY INTEGER, HOUR INTEGER, MIN INTEGER, SEC INTEGER);");

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

    /**
     * Delete all rows in picture table
     */
    public void emptyPTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + PICTURES_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Inserts a new row of picture details into picture table
     *
     * @param absPath photo path of picture
     * @param categoryName category of picture
     * @param label label of picture
     * @param position of picture
     * @param year year of picture added
     * @param month month of picture added
     * @param day day of picture added
     * @param hour hour of picture added
     * @param min min of picture added
     * @param sec sec of picture added
     * @return true if picture details were successfully inserted
     */
    public boolean insertNewRowPTable(String absPath, String categoryName, String label, String position, int year, int month, int day, int hour, int min, int sec) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_P_2, absPath);
        contentValues.put(COL_P_3, categoryName);
        contentValues.put(COL_P_4, label);
        contentValues.put(COL_P_5, position);
        contentValues.put(COL_P_6, year);
        contentValues.put(COL_P_7, month);
        contentValues.put(COL_P_8, day);
        contentValues.put(COL_P_9, hour);
        contentValues.put(COL_P_10, min);
        contentValues.put(COL_P_11, sec);

        long result = db.insert(PICTURES_TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        }
        return true;
    }

    /**
     * Get all rows of picture details in the picture table
     *
     * @return cursor of picture table
     */
    public Cursor getAllDataPTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + PICTURES_TABLE_NAME, null);
        return res;
    }

    /**
     * Get all rows of pictures with corresponding category name
     *
     * @param categoryName category name of the pictures needed
     * @return cursor of picture table
     */
    public Cursor getBasedOnCategoryPTable(String categoryName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.query(PICTURES_TABLE_NAME, null, "CATEGORYNAME = ?", new String[] {categoryName}, null, null, null);
        return res;
    }

    /**
     * Delete all rows in category table
     */
    public void emptyCTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CATEGORIES_TABLE_NAME, null, null);
    }

    /**
     * Inserts a new row of category into category table
     *
     * @param categoryName category of picture
     * @return true if picture details were successfully inserted
     */
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

    /**
     * Get all rows of category in the category table
     *
     * @return cursor of category table
     */
    public Cursor getAllDataCTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + CATEGORIES_TABLE_NAME, null);
        return res;
    }


}