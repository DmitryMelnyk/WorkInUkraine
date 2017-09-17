package com.dmelnyk.workinukraine.db;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.models.VacancyModel;

/**
 * Created by d264 on 7/19/17.
 */

public class DbItems {

    public static ContentValues createVacancyFavoriteItem(boolean isFavorite, VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchSites.Columns.DATE, vacancy.date());
        values.put(Tables.SearchSites.Columns.IS_FAVORITE, isFavorite);
        values.put(Tables.SearchSites.Columns.TIME_STATUS, vacancy.timeStatus());
        values.put(Tables.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(Tables.SearchSites.Columns.TITLE, vacancy.title());
        values.put(Tables.SearchSites.Columns.URL, vacancy.url());
        values.put(Tables.SearchSites.Columns.SITE, vacancy.site());

        return values;
    }

    public static ContentValues createVacancyNewItem(boolean isNew, VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchSites.Columns.DATE, vacancy.date());
        values.put(Tables.SearchSites.Columns.IS_FAVORITE, vacancy.isFavorite());
        values.put(Tables.SearchSites.Columns.TIME_STATUS, isNew);
        values.put(Tables.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(Tables.SearchSites.Columns.TITLE, vacancy.title());
        values.put(Tables.SearchSites.Columns.SITE, vacancy.site());
        values.put(Tables.SearchSites.Columns.URL, vacancy.url());

        return values;
    }

    public static ContentValues createRequestItem(
            String request, int vacanciesCount, int newVacanciesCount, long updated) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchRequest.Columns.REQUEST, request);
        values.put(Tables.SearchRequest.Columns.VACANCIES, vacanciesCount);
        values.put(Tables.SearchRequest.Columns.NEW_VACANCIES, newVacanciesCount);
        values.put(Tables.SearchRequest.Columns.UPDATED, updated);

        return values;
    }
}
