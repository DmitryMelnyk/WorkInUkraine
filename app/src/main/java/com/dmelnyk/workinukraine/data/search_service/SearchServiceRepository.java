package com.dmelnyk.workinukraine.data.search_service;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyContainer;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 7/25/17.
 */

public class SearchServiceRepository implements ISearchServiceRepository {

    private final BriteDatabase db;

    private static final String REQUEST_TABLE = Tables.SearchRequest.TABLE_REQUEST;
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
    public Observable<List<RequestModel>> getRequests() {
        Timber.d("\nloadRequestList()");
        return db.createQuery(REQUEST_TABLE, "SELECT * FROM " + REQUEST_TABLE)
                .mapToList(RequestModel.MAPPER);
    }

    @Override
    public void saveVacancies(List<VacancyContainer> allVacancies) throws Exception {
        Timber.d("Found %d vacancies", allVacancies.size());
        // don't do any changes if no vacancies has found
//        if (allVacancies.isEmpty()) return

        String request = allVacancies.get(0).getVacancy().request();
        // Counting previous new vacancies
        int previousNewVacancies = getPreviousNewVacanciesCount(request);

        // Getting previous vacancies
        String table = Tables.SearchSites.TABLE_ALL_SITES;
        List<VacancyContainer> oldVacancies = getPreviousVacancies(table, request);


        // Counting new vacancies
        int newVacanciesCount = 0;
        if (!oldVacancies.isEmpty()) {
            List<VacancyContainer> newVacancies = extractNewVacancy(oldVacancies, allVacancies);
            newVacanciesCount = newVacancies.size();

            Timber.d("new vacancies =", newVacancies);
            if (!newVacancies.isEmpty()) {
                // clearing new and recent vacancies
                clearVacanciesFromFavNewRecTable(Tables.SearchSites.TYPE_RECENT, request);
                // write new vacancies with TYPE_NEW;
                writeVacanciesToFavNewRecTable(Tables.SearchSites.TYPE_NEW, newVacancies);
            }

            // updating previous vacancies
            updateVacancies(request, allVacancies);
        } else {
            // all vacancies are new
            writeVacanciesToFavNewRecTable(Tables.SearchSites.TYPE_NEW, allVacancies);
            newVacanciesCount = allVacancies.size();
            // writing all vacancies to corresponding table
            writeVacanciesToSitesTable(allVacancies);

        }

        long updatingTime = System.currentTimeMillis();
        updateRequestTable(
                request, allVacancies.size(), newVacanciesCount + previousNewVacancies, updatingTime);

        db.close();
    }

    private int getPreviousNewVacanciesCount(String request) {
        Cursor newVacanciesCursor = db.query(
                SELECT_ALL_FROM + Tables.SearchSites.TABLE_FAV_NEW_REC
                + WHERE_ + "'" + request + "' AND "
                + Tables.SearchSites.Columns.TYPE + "='" + Tables.SearchSites.TYPE_NEW + "'");
        int previousNewVacancies = newVacanciesCursor.getCount();
        newVacanciesCursor.close();
        Timber.d("Previous new vacancies count=" + previousNewVacancies);

        return previousNewVacancies;
    }

    @NonNull
    private List<VacancyContainer> getPreviousVacancies(String table, String request) throws Exception {
        List<VacancyContainer> oldVacancies = new ArrayList<>();

        Cursor cursor = db.query(SELECT_ALL_FROM + table + WHERE_ + "'" + request + "'");
        if (cursor.moveToFirst()) {
            do {
                VacancyContainer vacancy = VacancyContainer.MAPPER.apply(cursor);
                oldVacancies.add(vacancy);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return oldVacancies;
    }

    // Clears only vacancies from those resources that we've had result.
//    private void updateVacancies(String request, List<VacancyContainer> allVacancies) {
//        // 5 - number of searching sites
//        for (String type : Tables.SearchSites.TYPE_SITES) {
//            boolean isTypeInVacancies = false;
//
//            for (VacancyContainer vc : allVacancies) {
//                if (vc.getType().equals(type)) {
//                    isTypeInVacancies = true;
//                    break;
//                }
//            }
//
//            if (isTypeInVacancies) {
//                // clears vacancies with proper type
//                Timber.d("\nclearing all vacancies with request=%s and type=%s", request, type);
//                db.delete(Tables.SearchSites.TABLE_ALL_SITES, Tables.SearchSites.Columns.REQUEST
//                        + " = '" + request + "' AND "
//                        + Tables.SearchSites.Columns.TYPE + " ='" + type + "'");
//            }
//        }
//    }
    private void updateVacancies(String request, List<VacancyContainer> allVacancies) throws Exception {
        // finding the types we have in allVacancies.
        // In case we didn't receive response from proper server
        // we don't need to remove that vacancies (because next time
        // those vacancy will be new. We don't want to notify user
        // about this "old" new vacancies)
        Set<String> siteTypes = new HashSet<>();
        for (VacancyContainer vc : allVacancies) {
            siteTypes.add(vc.getType());
        }

        for (String type : siteTypes) {
            List<VacancyContainer> oldVacancies = getVacancies(request, type);
            for (VacancyContainer vc : oldVacancies) {
                if (allVacancies.contains(vc)) {
                    // update only date in vacancy
                    VacancyContainer newVacancies = allVacancies.get(allVacancies.indexOf(vc));
                    updateVacancyWithDate(vc, newVacancies.getVacancy().date());
                } else {
                    // remove old vacancies
                    deleteVacancy(vc);
                }
            }
        }
    }

    private void updateVacancyWithDate(VacancyContainer vc, String date) {
        db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                + " SET " + Tables.SearchSites.Columns.DATE
                + " ='" + date + "' WHERE " + Tables.SearchSites.Columns.URL
                + " ='" + vc.getVacancy().url() + "'");
    }

    private void deleteVacancy(VacancyContainer vc) {
        Timber.d("Removing old vacancy=", vc);
        db.delete(Tables.SearchSites.TABLE_ALL_SITES, " WHERE "
                + Tables.SearchSites.Columns.URL + " ='" + vc.getVacancy().url() + "'");
    }

    private List<VacancyContainer> getVacancies(String request, String type) throws Exception {
        Cursor cursor = db.query("SELECT * FROM " + Tables.SearchSites.TABLE_ALL_SITES
                + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='"
                + request + "' AND " + Tables.SearchSites.Columns.TYPE + " ='"
                + type + "'");

        List<VacancyContainer> oldVacancies = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                VacancyContainer vacancy = VacancyContainer.MAPPER.apply(cursor);
                oldVacancies.add(vacancy);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return oldVacancies;
    }

    private void clearVacanciesFromFavNewRecTable(String type, String request) {
        Timber.d("\nclearing Recent vacancies with request=%s", request);
        db.delete(Tables.SearchSites.TABLE_FAV_NEW_REC, Tables.SearchSites.Columns.REQUEST + "= '" + request
                + "' AND " + Tables.SearchSites.Columns.TYPE + "= '" + type + "'");
    }

    public void updateRequestTable(
            String request, int vacancyCount, int newVacanciesCount, long lastUpdateTime) {
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

    private void writeVacanciesToSitesTable(List<VacancyContainer> list) {
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
