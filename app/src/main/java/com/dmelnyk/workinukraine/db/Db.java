package com.dmelnyk.workinukraine.db;

import android.database.Cursor;

/**
 * Created by d264 on 7/20/17.
 */

public final class Db {

    public static int getInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    public static long getLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndexOrThrow(columnName));
    }

    public static String getString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    private Db() {
        throw new AssertionError("This class has no instance!");
    }
}
