package com.example.franc.testcamera.SQLiteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by franc on 19/6/2019.
 */

public class NotesDatabaseHelper extends SQLiteOpenHelper {
    //database values
    private static final int versionNumber = 1;
    private static final String DATABASE_NAME = "notes.db";

    //ID | title | description | l margin | r margin | t margin | b margin | width | height
    private String TABLE_NAME = "notes_table";
    private String COL_1 = "ID";
    private String COL_2 = "TITLE";
    private String COL_3 = "DES";
    private String COL_4 = "LMARGIN";
    private String COL_5 = "RMARGIN";
    private String COL_6 = "TMARGIN";
    private String COL_7 = "BMARGIN";
    private String COL_8 = "WIDTH";
    private String COL_9 = "HEIGHT";

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, versionNumber);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //execSQL takes in a query to do whatever u want it to do
        //CREATE TABLE "table name", creates the table itself

        //Creates notes table
        db.execSQL("CREATE TABLE IF NOT EXIST " + TABLE_NAME +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DES TEXT, " +
                "LMARGIN INTEGER, RMARGIN INTEGER, TMARGIN INTEGER, BMARGIN INTEGER, " +
                "WIDTH INTEGER, HEIGHT INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will wipe out data very time version is incremented
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

        // Contentvalues are key value fairs
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

    public Integer deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {Integer.toString(id)});
    }
}
