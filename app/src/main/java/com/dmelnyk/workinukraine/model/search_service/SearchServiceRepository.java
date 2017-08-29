package com.dmelnyk.workinukraine.model.search_service;

import android.database.Cursor;
import android.util.Log;

import com.dmelnyk.workinukraine.data.VacancyContainer;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by d264 on 7/25/17.
 */

public class SearchServiceRepository implements ISearchServiceRepository {

    private final BriteDatabase db;

    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String WHERE_ = " WHERE "
            + Tables.SearchSites.Columns.REQUEST + " = ";

    public SearchServiceRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public void closeDb() {
        Timber.d("\nClosing database");
        db.close();
    }

    @Override
    public void saveVacancies(List<VacancyContainer> allVacancies) {
        Log.e("!!!", "map=" + allVacancies);
        // don't do any changes if no vacancies has found
        if (allVacancies.isEmpty()) return;

        String table = Tables.SearchSites.TABLE_ALL_SITES;
        String request = allVacancies.get(0).getVacancy().request();

        Cursor cursor = db.query(SELECT_ALL_FROM + table + WHERE_ + "'" + request + "'");
        List<VacancyContainer> oldVacancies = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String type = Db.getString(cursor, Tables.SearchSites.Columns.TYPE);
                String title = Db.getString(cursor, Tables.SearchSites.Columns.TITLE);
                String date = Db.getString(cursor, Tables.SearchSites.Columns.DATE);
                String url = Db.getString(cursor, Tables.SearchSites.Columns.URL);

                VacancyModel vacancy = VacancyModel.builder()
                        .setRequest(request)
                        .setTitle(title)
                        .setDate(date)
                        .setUrl(url)
                        .build();

                 oldVacancies.add(VacancyContainer.create(vacancy, type));
            } while (cursor.moveToNext());
        }

        cursor.close();

        int newVacanciesCount = 0;

        Log.e("!!!", "old vacancies = " + oldVacancies);
        if (!oldVacancies.isEmpty()) {
            List<VacancyContainer> newVacancies = extractNewVacancy(oldVacancies, allVacancies);
            newVacanciesCount = newVacancies.size();

            Log.e("!!!", "new vacancies = " + newVacancies);
            if (!newVacancies.isEmpty()) {
                // clearing new and recent vacancies
                clearVacanciesFromFavNewRecTable(Tables.SearchSites.TYPE_RECENT, request);
                // write new vacancies with TYPE_NEW;
                writeVacanciesToFavNewRecTable(Tables.SearchSites.TYPE_NEW, newVacancies);
            }
        } else {
            // all vacancies are new
            Log.e("!!!", "Writing vacancies NEW " + allVacancies.size());
            writeVacanciesToFavNewRecTable(Tables.SearchSites.TYPE_NEW, allVacancies);
            newVacanciesCount = allVacancies.size();
        }

        if (newVacanciesCount == 0) return;
        // clear previous vacancies
        clearVacanciesFromAllTable(request);
        // write all vacancies to corresponding table
        writeVacanciesToAllTable(allVacancies);
        // updating Requests table
        updateRequestTable(
                request, allVacancies.size(), newVacanciesCount, System.currentTimeMillis());
    }

    private void clearVacanciesFromAllTable(String request) {
        Timber.d("\nclearing all vacancies with request=%s", request);
        db.delete(Tables.SearchSites.TABLE_ALL_SITES, Tables.SearchSites.Columns.REQUEST
                + " = '" + request + "'");
    }

    private void clearVacanciesFromFavNewRecTable(String type, String request) {
        Timber.d("\nclearing Recent vacancies with request=%s", request);
        db.delete(Tables.SearchSites.TABLE_FAV_NEW_REC, Tables.SearchSites.Columns.REQUEST + "= '" + request
                + "' AND " + Tables.SearchSites.Columns.TYPE + "= '" + type + "'");
    }

    public void updateRequestTable(
            String request, int vacancyCount, int newVacanciesCount, long lastUpdateTime) {
        Log.e("444", "new vacancies=" + newVacanciesCount);
        Timber.d("\nUpdating request info. Request=%s, vacancyCount=%d, newVacanciesCount=%d, lastUpdateTime=%d",
                request, vacancyCount, newVacanciesCount, lastUpdateTime);

        db.update(Tables.SearchRequest.TABLE_REQUEST,
                DbItems.createRequestItem(request, vacancyCount, newVacanciesCount, lastUpdateTime),
                Tables.SearchRequest.Columns.REQUEST + " ='"
                + request + "'");
    }

    private List<VacancyContainer> extractNewVacancy(List<VacancyContainer> oldVacancies,
                                                      List<VacancyContainer> newVacancies) {
        List<VacancyContainer> copy = new ArrayList<>(newVacancies);
        for (VacancyContainer oldVacancy : oldVacancies) {
            copy.remove(oldVacancy);
        }

        return copy;
    }

    private void writeVacanciesToAllTable(List<VacancyContainer> list) {
        Timber.d("\nWriting into All table %d vacancies", list.size());
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (VacancyContainer vacancyContainer : list) {
                db.insert(Tables.SearchSites.TABLE_ALL_SITES,
                        DbItems.createVacancyContainerAllItem(vacancyContainer));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private void writeVacanciesToFavNewRecTable(String type, List<VacancyContainer> newVacancies) {
        Timber.d("\nWriting into FavNewRec table= %d vacancies", newVacancies.size());
        Log.e("111", "writing vacancies to " + type);
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (VacancyContainer vacancyContainer : newVacancies) {
                db.insert(Tables.SearchSites.TABLE_FAV_NEW_REC,
                        DbItems.createVacancyContainerFavNewRecItem(type, vacancyContainer));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
