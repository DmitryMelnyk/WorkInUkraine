package com.dmelnyk.workinukraine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by d264 on 7/19/17.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_SEARCH_REQUEST_TABLE = ""
            + "CREATE TABLE " + DbContract.SearchRequest.TABLE_REQUEST + "("
            + DbContract.SearchRequest.Columns.REQUEST + " TEXT NOT NULL PRIMARY KEY, "
            + DbContract.SearchRequest.Columns.VACANCIES + " INTEGER NOT NULL, "
            + DbContract.SearchRequest.Columns.NEW_VACANCIES + " INTEGER NOT NULL, "
            + DbContract.SearchRequest.Columns.UPDATED + " INTEGER NOT NULL)";

    private static final String CREATE_VACANCIES_TABLE = "CREATE TABLE "
            + DbContract.SearchSites.TABLE_ALL_SITES + "("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DbContract.SearchSites.Columns.REQUEST + " TEXT NOT NULL, "
            + DbContract.SearchSites.Columns.SITE + " TEXT NOT NULL, "
            + DbContract.SearchSites.Columns.TITLE + " TEXT NOT NULL, "
            + DbContract.SearchSites.Columns.DATE + " TEXT, "
            + DbContract.SearchSites.Columns.URL + " TEXT NOT NULL, "
            + DbContract.SearchSites.Columns.IS_FAVORITE + " INTEGER NOT NULL, "
            + DbContract.SearchSites.Columns.TIME_STATUS + " INTEGER NOT NULL);";

    public DbOpenHelper(Context context) {
        super(context, "main.db", null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creates table that contains request
        db.execSQL(CREATE_SEARCH_REQUEST_TABLE);

        // creates table that contains all vacancies
        db.execSQL(CREATE_VACANCIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
