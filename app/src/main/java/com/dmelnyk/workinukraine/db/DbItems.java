package com.dmelnyk.workinukraine.db;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.models.VacancyModel;

/**
 * Created by d264 on 7/19/17.
 */

public class DbItems {

    public static ContentValues createVacancyFavoriteItem(boolean isFavorite, VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(DbContract.SearchSites.Columns.DATE, vacancy.date());
        values.put(DbContract.SearchSites.Columns.IS_FAVORITE, isFavorite);
        values.put(DbContract.SearchSites.Columns.TIME_STATUS, vacancy.timeStatus());
        values.put(DbContract.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(DbContract.SearchSites.Columns.TITLE, vacancy.title());
        values.put(DbContract.SearchSites.Columns.URL, vacancy.url());
        values.put(DbContract.SearchSites.Columns.SITE, vacancy.site());

        return values;
    }

    // TODO: remove
    public static ContentValues createVacancyNewItem(boolean isNew, VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(DbContract.SearchSites.Columns.DATE, vacancy.date());
        values.put(DbContract.SearchSites.Columns.IS_FAVORITE, vacancy.isFavorite());
        values.put(DbContract.SearchSites.Columns.TIME_STATUS, isNew);
        values.put(DbContract.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(DbContract.SearchSites.Columns.TITLE, vacancy.title());
        values.put(DbContract.SearchSites.Columns.SITE, vacancy.site());
        values.put(DbContract.SearchSites.Columns.URL, vacancy.url());

        return values;
    }

    public static ContentValues createRequestItem(
            String request, int vacanciesCount, int newVacanciesCount, long updated) {
        final ContentValues values = new ContentValues();
        values.put(DbContract.SearchRequest.Columns.REQUEST, request);
        values.put(DbContract.SearchRequest.Columns.VACANCIES, vacanciesCount);
        values.put(DbContract.SearchRequest.Columns.NEW_VACANCIES, newVacanciesCount);
        values.put(DbContract.SearchRequest.Columns.UPDATED, updated);

        return values;
    }

    public static ContentValues createVacancyItem(VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(DbContract.SearchSites.Columns.DATE, vacancy.date());
        values.put(DbContract.SearchSites.Columns.IS_FAVORITE, vacancy.isFavorite());
        values.put(DbContract.SearchSites.Columns.TIME_STATUS, vacancy.timeStatus());
        values.put(DbContract.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(DbContract.SearchSites.Columns.TITLE, vacancy.title());
        values.put(DbContract.SearchSites.Columns.SITE, vacancy.site());
        values.put(DbContract.SearchSites.Columns.URL, vacancy.url());

        return values;
    }
}
