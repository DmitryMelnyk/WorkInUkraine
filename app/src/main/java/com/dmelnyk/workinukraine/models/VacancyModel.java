package com.dmelnyk.workinukraine.models;

import android.database.Cursor;
import android.os.Parcelable;

import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.utils.LocaleUtil;
import com.google.auto.value.AutoValue;

import java.util.List;

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
    public abstract String site();
    public abstract boolean isFavorite();
    public abstract int timeStatus(); // 1 - new, 0 - recent, -1 - old

    public static Builder builder() {
        return new AutoValue_VacancyModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setRequest(String request);
        public abstract Builder setTitle(String title);
        public abstract Builder setDate(String date);
        public abstract Builder setUrl(String url);
        public abstract Builder setSite(String site);
        public abstract Builder setIsFavorite(boolean isFavorite);
        public abstract Builder setTimeStatus(int timeStatus);
        public abstract VacancyModel build();
    }

    public static Function<Cursor, VacancyModel> MAPPER = new Function<Cursor, VacancyModel>() {
        @Override public VacancyModel apply(Cursor cursor) {
            String request = Db.getString(cursor, DbContract.SearchSites.Columns.REQUEST);
            String title = Db.getString(cursor, DbContract.SearchSites.Columns.TITLE);
            String date = Db.getString(cursor, DbContract.SearchSites.Columns.DATE);
            String url = Db.getString(cursor, DbContract.SearchSites.Columns.URL);
            String site = Db.getString(cursor, DbContract.SearchSites.Columns.SITE);
            boolean isFavorite = Db.getBoolean(cursor, DbContract.SearchSites.Columns.IS_FAVORITE);
            int timeStatus = Db.getInt(cursor, DbContract.SearchSites.Columns.TIME_STATUS);

            return VacancyModel.builder()
                    .setDate(LocaleUtil.convertToProperLanguage(date))
                    .setIsFavorite(isFavorite)
                    .setRequest(request)
                    .setSite(site)
                    .setTimeStatus(timeStatus)
                    .setTitle(title)
                    .setUrl(url)
                    .build();
        }
    };

    public VacancyModel getUpdatedFavoriteVacancy() {
        return VacancyModel.builder()
                .setDate(date())
                .setIsFavorite(!isFavorite())
                .setRequest(request())
                .setSite(site())
                .setTimeStatus(timeStatus())
                .setTitle(title())
                .setUrl(url())
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        VacancyModel model = (VacancyModel) obj;
        return this.url().equals(model.url());
    }
}
