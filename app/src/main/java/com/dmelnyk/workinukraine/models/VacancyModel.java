package com.dmelnyk.workinukraine.models;

import android.database.Cursor;
import android.os.Parcelable;

import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.Tables;
import com.google.auto.value.AutoValue;

import io.reactivex.functions.Function;

/**
 * Created by d264 on 7/20/17.
 */

@AutoValue
public abstract class VacancyModel implements Parcelable {
    public abstract String request();
    public abstract String title();
    public abstract String date();
    public abstract String url();

    public static Builder builder() {
        return new AutoValue_VacancyModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setRequest(String request);
        public abstract Builder setTitle(String title);
        public abstract Builder setDate(String date);
        public abstract Builder setUrl(String url);
        public abstract VacancyModel build();
    }

    public static Function<Cursor, VacancyModel> MAPPER = new Function<Cursor, VacancyModel>() {
        @Override public VacancyModel apply(Cursor cursor) {
            String request = Db.getString(cursor, Tables.SearchSites.Columns.REQUEST);
            String title = Db.getString(cursor, Tables.SearchSites.Columns.TITLE);
            String date = Db.getString(cursor, Tables.SearchSites.Columns.DATE);
            String url = Db.getString(cursor, Tables.SearchSites.Columns.URL);
            return VacancyModel.builder()
                    .setRequest(request)
                    .setTitle(title)
                    .setDate(date)
                    .setUrl(url)
                    .build();
        }
    };
}
