package com.dmelnyk.workinukraine.model.search_service;

import android.util.Log;

import com.dmelnyk.workinukraine.data.VacancyModel;
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
    private static final String DELETE_FROM_TABLE = "DELETE FROM ";
    private static final String WHERE_REQUEST_EQUALS = " WHERE "
            + Tables.SearchSites.Columns.REQUEST + " = ";

    public SearchServiceRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public void clearNewTable() {
        Timber.d("\nClearing NEW table");
        db.execute(DELETE_FROM_TABLE + Tables.SearchSites.NEW);
    }

    @Override
    public void closeDb() {
        Timber.d("\nClosing database");
        db.close();
    }

    @Override
    public void saveVacancies(String table, List<VacancyModel> list) {
        Timber.d("\nSaving vacancies into table %s", table);
        if (list.isEmpty() || list == null) return;

        String REQUEST = list.get(0).request();
        db.createQuery(table, SELECT_ALL_FROM + table + WHERE_REQUEST_EQUALS + "'" + REQUEST + "'")
                .mapToList(VacancyModel.MAPPER)
                .subscribe(
                        oldVacancies -> {
                            Log.e("!!!", "old vacancies = " + oldVacancies);
                            if (!oldVacancies.isEmpty()) {
                                List<VacancyModel> newVacancies = extractNewVacancy(oldVacancies, list);
                                // write new vacancies to Tables.SearchSites.NEW
                                Log.e("!!!", "new vacancies = " + newVacancies);
                                if (!newVacancies.isEmpty()) {
                                    // write new vacancies to NEW and RECENT tables;
                                    writeVacanciesToDb(Tables.SearchSites.NEW, newVacancies);
                                    clearOldVacancies(Tables.SearchSites.RECENT, REQUEST);
                                    writeVacanciesToDb(Tables.SearchSites.RECENT, newVacancies);
                                }
                            }
                            // clear previous vacancies
                            clearOldVacancies(table, REQUEST);
                            // write all vacancies to corresponding table
                            writeVacanciesToDb(table, list);
                        },
                        throwable -> Timber.e(throwable)
                );
    }

    private void clearOldVacancies(String table, String request) {
        Timber.d("\nclearing table=%s with request=%s", table, request);
        db.delete(table, Tables.SearchSites.Columns.REQUEST + "= '" + request + "'");
    }

    private List<VacancyModel> extractNewVacancy(List<VacancyModel> oldVacancies,
                                                      List<VacancyModel> newVacancies) {
        List<VacancyModel> copy = new ArrayList<>(newVacancies);
        for (VacancyModel oldVacancy : oldVacancies) {
            copy.remove(oldVacancy);
        }
        return copy;
    }

    private void writeVacanciesToDb(String table, List<VacancyModel> list) {
        Timber.d("\nWriting into table=%s %d vacancies", table, list.size());
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (VacancyModel vacancy : list) {
                db.insert(table, DbItems.createVacancyItem(vacancy));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
