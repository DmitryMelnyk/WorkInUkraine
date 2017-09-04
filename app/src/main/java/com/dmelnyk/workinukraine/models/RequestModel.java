package com.dmelnyk.workinukraine.models;

import android.database.Cursor;
import android.os.Parcelable;

import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.Tables;
import com.google.auto.value.AutoValue;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by d264 on 7/20/17.
 */

@AutoValue
public abstract class RequestModel implements Parcelable {
    public abstract String request();
    public abstract int vacanciesCount();
    public abstract int newVacanciesCount();
    public abstract long updated();

    public static RequestModel create(
            String request, int vacanciesCount, int newVacanciesCount, long updated) {
        return new AutoValue_RequestModel(request, vacanciesCount, newVacanciesCount, updated);
    }

    public static Function<Cursor, RequestModel> MAPPER = new Function<Cursor, RequestModel>() {
        @Override public RequestModel apply(@NonNull Cursor cursor) throws Exception {
            String request = Db.getString(cursor, Tables.SearchRequest.Columns.REQUEST);
            int vacanciesCount = Db.getInt(cursor, Tables.SearchRequest.Columns.VACANCIES);
            int newVacanciesCount = Db.getInt(cursor, Tables.SearchRequest.Columns.NEW_VACANCIES);
            long updated = Db.getLong(cursor, Tables.SearchRequest.Columns.UPDATED);
            return create(request, vacanciesCount, newVacanciesCount, updated);
        }
    };
}
