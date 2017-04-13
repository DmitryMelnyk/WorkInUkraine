package com.dmelnyk.workinukraine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dmelnyk.workinukraine.db.JobDbSchema.JobTable;

/**
 * Created by dmitry on 26.01.17.
 */


public class DBHelper extends SQLiteOpenHelper {
    public static final int Version = 1;
    public static final String DB_NAME = "jobBase.db";

    public DBHelper(Context context) {
        super(context, DB_NAME,  null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating 5 tables to save data from each search site
        for (String tableName : JobTable.NAMES) {
            db.execSQL("CREATE TABLE " + tableName + "(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    JobTable.Columns.TITLE + " TEXT, " +
                    JobTable.Columns.DATE + " TEXT, " +
                    JobTable.Columns.URL + " TEXT);");
        }

        // creating table for saving "favorite" jobs
        db.execSQL("CREATE TABLE " + JobTable.FAVORITE + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                JobTable.Columns.TITLE + " TEXT, " +
                JobTable.Columns.DATE + " TEXT, " +
                JobTable.Columns.URL + " TEXT);");

        // creating table for saving "recent" jobs
        db.execSQL("CREATE TABLE " + JobTable.RECENT + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                JobTable.Columns.TITLE + " TEXT, " +
                JobTable.Columns.DATE + " TEXT, " +
                JobTable.Columns.URL + " TEXT);");

        // creating table for saving "new" jobs
        db.execSQL("CREATE TABLE " + JobTable.NEW + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                JobTable.Columns.TITLE + " TEXT, " +
                JobTable.Columns.DATE + " TEXT, " +
                JobTable.Columns.URL + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
