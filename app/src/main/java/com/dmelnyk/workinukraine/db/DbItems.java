package com.dmelnyk.workinukraine.db;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.models.VacancyContainer;
import com.dmelnyk.workinukraine.models.VacancyModel;

/**
 * Created by d264 on 7/19/17.
 */

public class DbItems {

    public static ContentValues createVacancyContainerAllItem(VacancyContainer vacancyContainer) {
        VacancyModel vacancy = vacancyContainer.getVacancy();
        String siteType = vacancyContainer.getType();

        final ContentValues values = getVacancyContainerContentValue(vacancy);
        values.put(Tables.SearchSites.Columns.TYPE, siteType);
        return values;
    }

    public static ContentValues createVacancyContainerFavNewRecItem(String newType,
            VacancyContainer vacancyContainer) {
        VacancyModel vacancy = vacancyContainer.getVacancy();

        return createVacancyContainerFavNewRecItem(newType, vacancyContainer.getVacancy());
    }

    public static ContentValues createVacancyContainerFavNewRecItem(
            String type, VacancyModel vacancy) {

        final ContentValues value = getVacancyContainerContentValue(vacancy);
        value.put(Tables.SearchSites.Columns.TYPE, type);

        return value;
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

    private static ContentValues getVacancyContainerContentValue(VacancyModel vacancy) {
        final ContentValues values = new ContentValues();
        values.put(Tables.SearchSites.Columns.REQUEST, vacancy.request());
        values.put(Tables.SearchSites.Columns.TITLE, vacancy.title());
        values.put(Tables.SearchSites.Columns.DATE, vacancy.date());
        values.put(Tables.SearchSites.Columns.URL, vacancy.url());
        values.put(Tables.SearchSites.Columns.IS_FAVORITE, vacancy.isFavorite());

        return values;
    }
}
