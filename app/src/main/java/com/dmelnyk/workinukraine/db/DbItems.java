package com.dmelnyk.workinukraine.db;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.data.VacancyModel;

/**
 * Created by d264 on 7/19/17.
 */

public class DbItems {

    public static ContentValues createVacancyItem(VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(Tables.SearchSites.Columns.TITLE, vacancy.title());
        values.put(Tables.SearchSites.Columns.DATE, vacancy.date());
        values.put(Tables.SearchSites.Columns.URL, vacancy.url());

        return values;
    }

    public static ContentValues createRequestItem(String request, int vacanciesCount, long updated) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchRequest.Columns.REQUEST, request);
        values.put(Tables.SearchRequest.Columns.VACANCIES, vacanciesCount);
        values.put(Tables.SearchRequest.Columns.UPDATED, updated);

        return values;
    }
}