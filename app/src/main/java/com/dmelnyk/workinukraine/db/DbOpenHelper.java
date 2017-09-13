package com.dmelnyk.workinukraine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by d264 on 7/19/17.
 */

public class DbOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    private static final String CREATE_SEARCH_REQUEST = ""
            + "CREATE TABLE " + Tables.SearchRequest.TABLE_REQUEST + "("
            + Tables.SearchRequest.Columns.REQUEST + " TEXT NOT NULL PRIMARY KEY, "
            + Tables.SearchRequest.Columns.VACANCIES + " INTEGER NOT NULL, "
            + Tables.SearchRequest.Columns.NEW_VACANCIES + " INTEGER NOT NULL, "
            + Tables.SearchRequest.Columns.UPDATED + " INTEGER NOT NULL)";

    public DbOpenHelper(Context context) {
        super(context, "main.db", null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creates table that contains request
        db.execSQL(CREATE_SEARCH_REQUEST);

        // creates table that contains all vacancies
        db.execSQL(createTableSql(Tables.SearchSites.TABLE_ALL_SITES));

        // creates table that contains TYPE_NEW, TYPE_RECENT, TYPE_FAVORITE vacancies
        db.execSQL(createTableSql(Tables.SearchSites.TABLE_FAV_NEW_REC));
    }

    private String createTableSql(String table) {
        return "CREATE TABLE " + table + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Tables.SearchSites.Columns.REQUEST + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.TYPE + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.TITLE + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.DATE + " TEXT, "
                + Tables.SearchSites.Columns.URL + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.IS_FAVORITE + " INTEGER NOT NULL);";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
