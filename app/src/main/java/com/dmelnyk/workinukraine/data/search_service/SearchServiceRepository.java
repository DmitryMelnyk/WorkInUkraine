package com.dmelnyk.workinukraine.data.search_service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import timber.log.Timber;

import static com.dmelnyk.workinukraine.data.vacancy_list.VacancyListRepository.SHARED_PREF;

/**
 * Created by d264 on 7/25/17.
 */

public class SearchServiceRepository implements ISearchServiceRepository {

    private final BriteDatabase db;
    private final Context context;

    public static final String TABLE = Tables.SearchSites.TABLE_ALL_SITES;
    private static final String REQUEST_TABLE = Tables.SearchRequest.TABLE_REQUEST;
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String WHERE_ = " WHERE "
            + Tables.SearchSites.Columns.REQUEST + " = ";

    public SearchServiceRepository(BriteDatabase db, Context context) {
        this.db = db;
        this.context = context;
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
    public void saveVacancies(List<VacancyModel> allVacancies) throws Exception {
        Timber.d("Found %d vacancies", allVacancies.size());

        if (allVacancies.isEmpty()) return; // TODO update data in request table

        String request = allVacancies.get(0).request();

        if (shouldBeUpdated(request)) {
            updateTimeStatusVacancies(request);
        }

        // Getting previous vacancies
        List<VacancyModel> oldVacancies = getPreviousVacancies(request);

        // finding and writing new vacancies to db
        List<VacancyModel> newVacancies = extractNewVacancy(oldVacancies, allVacancies);
        Timber.d("new vacancies =", newVacancies);
        writeVacanciesToSitesTable(true, newVacancies);

        // updating date in previous vacancies
        updateVacanciesDate(request, allVacancies);

        // updating request table
        int newVacanciesCount = getNewVacanciesCount(request);
        long updatingTime = System.currentTimeMillis();
        updateRequestTable(request, allVacancies.size(), newVacanciesCount, updatingTime);

        db.close();
    }

    @NonNull
    private List<VacancyModel> getPreviousVacancies(String request) throws Exception {
        String table = Tables.SearchSites.TABLE_ALL_SITES;

        List<VacancyModel> oldVacancies = new ArrayList<>();

        Cursor cursor = db.query(SELECT_ALL_FROM + table + WHERE_ + "'" + request + "'");
        if (cursor.moveToFirst()) {
            do {
                VacancyModel vacancy = VacancyModel.MAPPER.apply(cursor);
                oldVacancies.add(vacancy);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return oldVacancies;
    }

    private void updateVacanciesDate(String request, List<VacancyModel> downloadedVacancies) throws Exception {
        Timber.d("updateVacanciesDate");
        // finding the types we have in downloadedVacancies.
        // In case we didn't receive response from proper server
        // we don't need to remove that vacancies (because next time
        // those vacancy will be new. We don't want to notify user
        // about this "old" new vacancies)
        Set<String> siteTypes = new HashSet<>();
        for (VacancyModel vacancy : downloadedVacancies) {
            siteTypes.add(vacancy.site());
        }
        Timber.d("siteTypes=" + siteTypes);

        for (String site : siteTypes) {
            List<VacancyModel> oldVacancies = getVacancies(request, site);
            for (VacancyModel vacancy : oldVacancies) {
                if (downloadedVacancies.contains(vacancy)) {
                    // update only date in vacancy
                    VacancyModel newVacancies = downloadedVacancies.get(downloadedVacancies.indexOf(vacancy));
                    updateVacancyWithDate(vacancy, newVacancies.date());
                } else {
                    // remove old vacancy
                    // TODO: in test mode. Don't remove old vacancies
//                    deleteVacancy(vacancy);
                }
            }
        }
    }

    private int getNewVacanciesCount(String request) {
        Cursor newVacanciesCursor = db.query(
                SELECT_ALL_FROM + Tables.SearchSites.TABLE_ALL_SITES
                        + WHERE_ + "'" + request + "' AND "
                        + Tables.SearchSites.Columns.TIME_STATUS + "=1");

        int newVacancies = newVacanciesCursor.getCount();
        newVacanciesCursor.close();
        Timber.d("New vacancies count=" + newVacancies);

        return newVacancies;
    }

    private boolean shouldBeUpdated(String request) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, 0);
        return preferences.getBoolean(request, false);
    }

    private void saveUpdatingTask(String request, boolean shouldBeUpdated) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, 0);
        preferences.edit().putBoolean(request, shouldBeUpdated).commit();
    }

    private void updateTimeStatusVacancies(String request) {
        // After watching 'new' vacancies - update them to 'recent' (timeStatus = -1)

        Cursor cursorNewVacancies = db.query("SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.TIME_STATUS + " =1"); // new
        if (cursorNewVacancies.getCount() > 0) {
            convertRecentToOldVacancies(request);
            convertNewToRecentVacancies(request);
        }

        cursorNewVacancies.close();
        saveUpdatingTask(request, false);
    }

    private void convertRecentToOldVacancies(String request) {
        db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                + " SET " + Tables.SearchSites.Columns.TIME_STATUS + "=-1"
                + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.TIME_STATUS + "=0");
    }

    private void convertNewToRecentVacancies(String request) {
        Timber.d("\nclearing New vacancies with request=%s", request);

        db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                + " SET " + Tables.SearchSites.Columns.TIME_STATUS + "=0" // convert to recent
                + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.TIME_STATUS + "=1"); // from new

        // updating Request's table
        db.execute("UPDATE " + Tables.SearchRequest.TABLE_REQUEST
                + " SET " + Tables.SearchRequest.Columns.NEW_VACANCIES
                + "=0 WHERE " + Tables.SearchRequest.Columns.REQUEST
                + "='" + request + "'");
    }

    private void updateVacancyWithDate(VacancyModel vacancy, String date) {
        db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                + " SET " + Tables.SearchSites.Columns.DATE
                + " ='" + date + "' WHERE " + Tables.SearchSites.Columns.URL
                + " ='" + vacancy.url() + "'");
    }

    private void deleteVacancy(VacancyModel vacancy) {
        Timber.d("Removing old vacancy=", vacancy);
        db.delete(Tables.SearchSites.TABLE_ALL_SITES, Tables.SearchSites.Columns.URL
                + " ='" + vacancy.url() + "'");
    }

    private List<VacancyModel> getVacancies(String request, String site) throws Exception {
        Cursor cursor = db.query("SELECT * FROM " + Tables.SearchSites.TABLE_ALL_SITES
                + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='"
                + request + "' AND " + Tables.SearchSites.Columns.SITE + " ='"
                + site + "'");

        List<VacancyModel> oldVacancies = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                VacancyModel vacancy = VacancyModel.MAPPER.apply(cursor);
                oldVacancies.add(vacancy);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return oldVacancies;
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

    private List<VacancyModel> extractNewVacancy(List<VacancyModel> oldVacancies,
                                                      List<VacancyModel> newVacancies) {
        List<VacancyModel> copy = new ArrayList<>(newVacancies);
        for (VacancyModel oldVacancy : oldVacancies) {
            copy.remove(oldVacancy);
        }

        return copy;
    }

    private void writeVacanciesToSitesTable(boolean isNew, List<VacancyModel> list) {
        Timber.d("\nWriting into All table %d vacancies", list.size());

        if (list.isEmpty()) return;
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (VacancyModel vacancy : list) {
                db.insert(Tables.SearchSites.TABLE_ALL_SITES,
                        DbItems.createVacancyNewItem(isNew, vacancy));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
