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
            + "CREATE TABLE " + Tables.SearchRequest.TABLE + "("
            + Tables.SearchRequest.Columns.REQUEST + " TEXT NOT NULL PRIMARY KEY, "
            + Tables.SearchRequest.Columns.VACANCIES + " INTEGER NOT NULL, "
            + Tables.SearchRequest.Columns.UPDATED + " INTEGER NOT NULL)";

    public DbOpenHelper(Context context) {
        super(context, "main.db", null /* factory */, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEARCH_REQUEST);

        for (String table : Tables.SearchSites.SITES) {
            db.execSQL(createTableSql(table));
        }

        db.execSQL(createTableSql(Tables.SearchSites.FAVORITE));
        db.execSQL(createTableSql(Tables.SearchSites.NEW));
        db.execSQL(createTableSql(Tables.SearchSites.RECENT));
    }

    private String createTableSql(String table) {
        return ""
                + "CREATE TABLE " + table + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Tables.SearchSites.Columns.REQUEST + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.TITLE + " TEXT NOT NULL, "
                + Tables.SearchSites.Columns.DATE + " TEXT, "
                + Tables.SearchSites.Columns.URL + " TEXT NOT NULL);";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
