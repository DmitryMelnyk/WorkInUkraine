package com.dmelnyk.workinukraine.ui.vacancy_list.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.business.IVacancyListInteractor;
import com.dmelnyk.workinukraine.utils.SharedPrefFilterUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyListRepository implements IVacancyListRepository {

    public static final String TABLE = DbContract.SearchSites.TABLE_ALL_SITES;
    private final BriteDatabase db;
    private final Context context;
    private final SharedPrefUtil sharedPrefUtil;
    private final SharedPrefFilterUtil filterUtil;
    private final String request;
    private Map<String, List<VacancyModel>> rawCache = new HashMap<>();

    public VacancyListRepository(BriteDatabase db, Context context, SharedPrefUtil util,
                                 SharedPrefFilterUtil filterUtil, String request) {
        this.db = db;
        this.context = context;
        this.request = request;
        this.sharedPrefUtil = util;
        this.filterUtil = filterUtil;
    }

    @Override
    public Single<Map<String, List<VacancyModel>>> getAllVacancies(String request) {
        Timber.d("\ngetAllVacancies, request =%s", request);

        if (shouldBeUpdated(request)) {
            updateTimeStatusVacancies(request);
        }

        // All vacancies
        Observable<List<VacancyModel>> allObservable =
                db.createQuery(TABLE, "SELECT * FROM "
                        + DbContract.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + DbContract.SearchSites.Columns.REQUEST + " ='" + request + "'")
                        .mapToList(VacancyModel.MAPPER);

        // New vacancies
        Observable<List<VacancyModel>> newObservable =
                db.createQuery(DbContract.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + DbContract.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS + " =1") // 1 - new vacancy
                        .mapToList(VacancyModel.MAPPER);

        // Recent vacancies
        Observable<List<VacancyModel>> recentObservable =
                db.createQuery(DbContract.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + DbContract.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS + " =0") // -1 - recent vacancy
                        .mapToList(VacancyModel.MAPPER);

        // Favorite vacancies
        Observable<List<VacancyModel>> favoriteObservable =
                db.createQuery(DbContract.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + DbContract.SearchSites.TABLE_ALL_SITES + " WHERE " +
                        DbContract.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + DbContract.SearchSites.Columns.IS_FAVORITE + " =1") // 1 - favorite vacancy
                        .mapToList(VacancyModel.MAPPER);

        return Observable.zip(allObservable, newObservable, recentObservable, favoriteObservable,
                (all, newest, recent, favorite) -> {
                    Map<String, List<VacancyModel>> result = new HashMap<>();

                    rawCache.put(IVacancyListInteractor.DATA_ALL, all);
                    rawCache.put(IVacancyListInteractor.DATA_NEW, newest);
                    rawCache.put(IVacancyListInteractor.DATA_RECENT, recent);
                    rawCache.put(IVacancyListInteractor.DATA_FAVORITE, favorite);

                    result.put(IVacancyListInteractor.DATA_ALL, filterVacancies(all, request));
                    result.put(IVacancyListInteractor.DATA_NEW, filterVacancies(newest, request));
                    result.put(IVacancyListInteractor.DATA_RECENT, filterVacancies(recent, request));
                    result.put(IVacancyListInteractor.DATA_FAVORITE, filterVacancies(favorite, request));

                    if (!newest.isEmpty()) {
                        saveUpdatingTask(request, true);
                        updateRequestTableData(request); // setting newVacancies count to 0
                    }

                    return result;
                }).firstOrError();
    }

    @Override
    public Single<Map<String, List<VacancyModel>>> getFilteredVacancies() {
        Map<String, List<VacancyModel>> filtered = new HashMap<>(rawCache);
        for (String key : filtered.keySet()) {
            filtered.put(key, filterVacancies(filtered.get(key), request));
        }

        return Single.fromCallable(() -> filtered);
    }

    @Override
    public void updateTimeStatusVacancies(String request) {
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

    @Override
    public Observable<List<VacancyModel>> getFavoriteVacancies(String request) {
        Log.d(getClass().getSimpleName(), "getFavoriteVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " +
                TABLE + " WHERE " + DbContract.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + DbContract.SearchSites.Columns.IS_FAVORITE + " =1") // 1 - favorite vacancy
                .mapToList(VacancyModel.MAPPER);
    }

    @Override
    public Completable removeFromFavorites(VacancyModel vacancy) {
        Timber.d("\nremoveFromFavorites: %s", vacancy);

        return Completable.fromCallable(() -> {
            ContentValues updatedVacancy = DbItems.createVacancyFavoriteItem(false, vacancy);
            db.update(TABLE, updatedVacancy,
                    DbContract.SearchSites.Columns.REQUEST + " = ? AND " +
                    DbContract.SearchSites.Columns.URL + " = ?",
                    new String[]{vacancy.request(), vacancy.url()});
            return true;
        });
    }

    @Override
    public Completable addToFavorite(VacancyModel vacancy) {

        Timber.d("\nSaving to TYPE_FAVORITE table %s", vacancy);
        Cursor cursor = db.query("SELECT * FROM "
                + DbContract.SearchSites.TABLE_ALL_SITES
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST + " ='" + vacancy.request()
                + "' AND " + DbContract.SearchSites.Columns.IS_FAVORITE + " =1"
                + " AND " + DbContract.SearchSites.Columns.URL
                + " = '" + vacancy.url() + "'");

        // don't save vacancy if it already exist in Favorite list
        if (!cursor.moveToFirst()) {
            cursor.close();
            return Completable.fromCallable(() -> {
                // updating vacancy in DbContract.SearchSites.TABLE_ALL_SITES
                ContentValues updatedVacancy = DbItems.createVacancyFavoriteItem(true, vacancy);
                db.update(TABLE, updatedVacancy,
                        DbContract.SearchSites.Columns.REQUEST + " = ? AND " +
                        DbContract.SearchSites.Columns.URL + " = ?",
                        new String[]{ vacancy.request(), vacancy.url()});
                return true;
            });
        } else {

            cursor.close();
            return Completable.error(new Exception("Vacancy already exist in list!"));
        }
    }

    @Override
    public String[] getNewTitles() {
        return context.getResources().getStringArray(R.array.tab_titles_with_new);
    }

    @Override
    public String[] getNewAndRecent() {
        return context.getResources().getStringArray(R.array.tab_titles_with_new_and_recent);
    }

    @Override
    public String[] getRecentTitles() {
        return context.getResources().getStringArray(R.array.tab_titles_with_recent);
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public void setIsFilterEnable(boolean isFilterEnable) {
        filterUtil.setFilterEnable(request, isFilterEnable);
    }

    @Override
    public Set<String> getFilterWords() {
        return filterUtil.getFilterWords(request);
    }

    @Override
    public void saveFilterWords(Set<String> words) {
        filterUtil.saveFilterWords(request, words);
    }

    @Override
    public boolean isFilterEnable() {
        return filterUtil.isFilterEnable(request);
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
    }

    private void updateRequestTableData(String request) {
        // updating Request's table
        Log.e("VLR", "updating request table");

        Cursor cursorRequest = db.query("SELECT * FROM " +  DbContract.SearchRequest.TABLE_REQUEST
                + " WHERE " + DbContract.SearchRequest.Columns.REQUEST + " ='" + request + "'");

        if (cursorRequest != null && cursorRequest.moveToFirst()) {
            int vacanciesCount = Db.getInt(cursorRequest, DbContract.SearchRequest.Columns.VACANCIES);

            ContentValues updatedRequestItem = DbItems.createRequestItem(
                    request, vacanciesCount, 0, System.currentTimeMillis());

            db.update(DbContract.SearchRequest.TABLE_REQUEST, updatedRequestItem,
                    DbContract.SearchRequest.Columns.REQUEST +" = ?", request);
        }

        cursorRequest.close();
    }

    private boolean shouldBeUpdated(String request) {
        return sharedPrefUtil.shouldBeUpdated(request);
    }

    private void saveUpdatingTask(String request, boolean shouldBeUpdated) {
        sharedPrefUtil.saveUpdatingTask(request, shouldBeUpdated);
    }

    private List<VacancyModel> filterVacancies(List<VacancyModel> list, String request) {
        boolean isFilterEnable = filterUtil.isFilterEnable(request);
        if (!isFilterEnable) return list;

        Set<String> filters = filterUtil.getFilterWords(request);
        if (!list.isEmpty() && !filters.isEmpty()) {
            List<VacancyModel> filteredVacancies = new ArrayList<>(list);

            for (VacancyModel vacancy : list) {
                for (String filter : filters) {
                    boolean vacancyContainsForbiddenWord =
                            vacancy.title().toLowerCase().contains(filter.toLowerCase());

                    if (vacancyContainsForbiddenWord) {
                        filteredVacancies.remove(vacancy);
                        break;
                    }
                }
            }

            return filteredVacancies;
        } else {
            return list;
        }
    }
}
