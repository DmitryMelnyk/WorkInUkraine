package com.dmelnyk.workinukraine.services.search.repository;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.SharedPrefFilterUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by d264 on 7/25/17.
 */

public class SearchServiceRepository implements ISearchServiceRepository {

    private static final String REQUEST_TABLE = DbContract.SearchRequest.TABLE_REQUEST;
    private static final String VACANCY_TABLE = DbContract.SearchSites.TABLE_ALL_SITES;
    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private static final String TABLE = DbContract.SearchSites.TABLE_ALL_SITES;
    private static final String WHERE_ = " WHERE " + DbContract.SearchSites.Columns.REQUEST + " = ";

    private final BriteDatabase db;
    private final SharedPrefUtil sharedPrefUtil;
    private final SharedPrefFilterUtil filterUtil;


    public SearchServiceRepository(
            BriteDatabase db,
            SharedPrefUtil sharedPrefUtil,
            SharedPrefFilterUtil filterUtil) {
        this.db = db;
        this.sharedPrefUtil = sharedPrefUtil;
        this.filterUtil = filterUtil;
    }

    @Override
    public void closeDb() {
        Timber.d("\nClosing database");
        db.close();
    }

    @Override
    public Single<List<RequestModel>> getRequests() {
        Timber.d("\nloadRequestList()");
        return db.createQuery(REQUEST_TABLE, "SELECT * FROM " + REQUEST_TABLE)
                .mapToList(RequestModel.MAPPER)
                .firstOrError();
    }

    @Override
    public void saveVacancies(List<VacancyModel> allVacancies) throws Exception {
        Timber.d("Found %d vacancies", allVacancies.size());

        if (allVacancies.isEmpty()) return;

        String request = allVacancies.get(0).request();

        if (shouldBeUpdated(request)) {
            updateTimeStatusVacancies(request);
        }

        // Getting previous vacancies
        List<VacancyModel> oldVacancies = getPreviousVacancies(request);

        List<VacancyModel> updatedVacancies = new ArrayList<>();
        for (VacancyModel vacancy : allVacancies) {
            VacancyModel.Builder vacancyBuilder = VacancyModel.builder()
                    .setDate(vacancy.date())
                    .setRequest(vacancy.request())
                    .setSite(vacancy.site())
                    .setTitle(vacancy.title())
                    .setUrl(vacancy.url());

            if (oldVacancies.contains(vacancy)) {
                // finding Vacancy to get isFavorite's and time's info
                int recentVacancyIndex = oldVacancies.indexOf(vacancy);
                VacancyModel recentVacancy = oldVacancies.get(recentVacancyIndex);

                vacancyBuilder
                        .setTimeStatus(recentVacancy.timeStatus())
                        .setIsFavorite(recentVacancy.isFavorite());
            } else {
                vacancyBuilder
                        .setTimeStatus(1) // new vacancy
                        .setIsFavorite(false);
            }

            updatedVacancies.add(vacancyBuilder.build());
        }

        // remove all previous vacancies with current request
        db.delete(VACANCY_TABLE, DbContract.SearchRequest.Columns.REQUEST + " = '" + request + "'");
        // writing vacancies to db
        writeVacanciesToDb(updatedVacancies);

        // updating request table
        int newVacanciesCount = getNewVacanciesCount(request);
        long updatingTime = System.currentTimeMillis();
        updateRequestTable(request, allVacancies.size(), newVacanciesCount, updatingTime);

        db.close();
    }

    @NonNull
    private List<VacancyModel> getPreviousVacancies(String request) throws Exception {
        String table = DbContract.SearchSites.TABLE_ALL_SITES;

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

    private int getNewVacanciesCount(String request) throws Exception {
        Cursor cursor = db.query(
                SELECT_ALL_FROM + DbContract.SearchSites.TABLE_ALL_SITES
                        + WHERE_ + "'" + request + "' AND "
                        + DbContract.SearchSites.Columns.TIME_STATUS + "=1");

        int newVacancies = cursor.getCount();
        boolean isFilterEnable = filterUtil.isFilterEnable(request);
        Set<String> filterWords = filterUtil.getFilterWords(request);

        if (isFilterEnable && !filterWords.isEmpty()) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                VacancyModel vacancy = VacancyModel.MAPPER.apply(cursor);
                for (String filter : filterWords) {
                    if (vacancy.title().contains(filter)) {
                        newVacancies--;
                        break;
                    }
                }
            }
        }
        cursor.close();

        Timber.d("New vacancies count=" + newVacancies);
        return newVacancies;
    }

    private boolean shouldBeUpdated(String request) {
        return sharedPrefUtil.shouldBeUpdated(request);
    }

    private void saveUpdatingTask(String request, boolean shouldBeUpdated) {
        sharedPrefUtil.saveUpdatingTask(request, shouldBeUpdated);
    }

    private void updateTimeStatusVacancies(String request) {
        // After watching 'new' vacancies - update them to 'recent' (timeStatus = -1)

        Cursor cursorNewVacancies = db.query("SELECT * FROM " + TABLE
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS + " =1"); // new
        if (cursorNewVacancies.getCount() > 0) {
            convertRecentToOldVacancies(request);
            convertNewToRecentVacancies(request);
        }

        cursorNewVacancies.close();
        saveUpdatingTask(request, false);
    }

    private void convertRecentToOldVacancies(String request) {
        db.execute("UPDATE " + DbContract.SearchSites.TABLE_ALL_SITES
                + " SET " + DbContract.SearchSites.Columns.TIME_STATUS + "=-1"
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS + "=0");
    }

    private void convertNewToRecentVacancies(String request) {
        Timber.d("\nclearing New vacancies with request=%s", request);

        db.execute("UPDATE " + DbContract.SearchSites.TABLE_ALL_SITES
                + " SET " + DbContract.SearchSites.Columns.TIME_STATUS + "=0" // convert to recent
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS + "=1"); // from new

        // updating Request's table
        db.execute("UPDATE " + DbContract.SearchRequest.TABLE_REQUEST
                + " SET " + DbContract.SearchRequest.Columns.NEW_VACANCIES
                + "=0 WHERE " + DbContract.SearchRequest.Columns.REQUEST
                + "='" + request + "'");
    }

    public void updateRequestTable(
            String request, int vacancyCount, int newVacanciesCount, long lastUpdateTime) {
        Timber.d("\nUpdating request info. Request=%s, vacancyCount=%d, newVacanciesCount=%d, lastUpdateTime=%d",
                request, vacancyCount, newVacanciesCount, lastUpdateTime);

        db.update(DbContract.SearchRequest.TABLE_REQUEST,
                DbItems.createRequestItem(request, vacancyCount, newVacanciesCount, lastUpdateTime),
                DbContract.SearchRequest.Columns.REQUEST + " ='"
                + request + "'");
    }

    private void writeVacanciesToDb(@NonNull List<VacancyModel> vacancies) {
        Timber.d("\nWriting into All table %d vacancies", vacancies.size());
        BriteDatabase.Transaction transaction = db.newTransaction();

        try{
            for (VacancyModel vacancy : vacancies) {
                db.insert(VACANCY_TABLE, DbItems.createVacancyItem(vacancy));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
