package com.dmelnyk.workinukraine.data.vacancy_list;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.business.vacancy_list.IVacancyListInteractor;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyListRepository implements IVacancyListRepository {

    public static final String TABLE = Tables.SearchSites.TABLE_ALL_SITES;
    public static final String SHARED_PREF = "shared_pref"; // for saving vacancy updating status
    private final BriteDatabase db;
    private final Context context;

    public VacancyListRepository(BriteDatabase db, Context context) {
        this.db = db;
        this.context = context;
    }

    @Override
    public Observable<Map<String, List<VacancyModel>>> getAllVacancies(String request) {
        Timber.d("\ngetAllVacancies, request =%s", request);

        if (shouldBeUpdated(request)) {
            updateTimeStatusVacancies(request);
        }
        // All vacancies
        Observable<List<VacancyModel>> allObservable =
                db.createQuery(TABLE, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + Tables.SearchSites.Columns.REQUEST + " ='" + request + "'")
                        .mapToList(VacancyModel.MAPPER);

        // New vacancies
        Observable<List<VacancyModel>> newObservable =
                db.createQuery(Tables.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + Tables.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + Tables.SearchSites.Columns.TIME_STATUS + " =1") // 1 - new vacancy
                        .mapToList(VacancyModel.MAPPER);

        // Recent vacancies
        Observable<List<VacancyModel>> recentObservable =
                db.createQuery(Tables.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_ALL_SITES + " WHERE "
                        + Tables.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + Tables.SearchSites.Columns.TIME_STATUS + " =0") // -1 - recent vacancy
                        .mapToList(VacancyModel.MAPPER);

        // Favorite vacancies
        Observable<List<VacancyModel>> favoriteObservable =
                db.createQuery(Tables.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_ALL_SITES + " WHERE " +
                        Tables.SearchSites.Columns.REQUEST + " ='" + request
                        + "' AND " + Tables.SearchSites.Columns.IS_FAVORITE + " =1") // 1 - favorite vacancy
                        .mapToList(VacancyModel.MAPPER);

        return Observable.zip(allObservable, newObservable, recentObservable, favoriteObservable,
                (all, newest, recent, favorite) -> {
                    Map<String, List<VacancyModel>> result = new HashMap<>();

                    result.put(IVacancyListInteractor.DATA_ALL, all);
                    result.put(IVacancyListInteractor.DATA_NEW, newest);
                    result.put(IVacancyListInteractor.DATA_RECENT, recent);
                    result.put(IVacancyListInteractor.DATA_FAVORITE, favorite);

                    Log.e("@@", "all=" + all.size());
                    Log.e("@@", "new=" + newest.size());
                    Log.e("@@", "recent=" + recent.size());
                    Log.e("@@", "favorite=" + favorite.size());

                    for (VacancyModel vacancy : all) {
                        Log.e("@@", "vacancy=" + vacancy);
                    }

                    if (!newest.isEmpty()) {
                        saveUpdatingTask(request, true);
                        updateRequestTableData(request); // setting newVacancies count to 0
                    }

                    db.close();
                    return result;
        });
    }

    @Override
    public void updateTimeStatusVacancies(String request) {
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

    @Override
    public Observable<List<VacancyModel>> getFavoriteVacancies(String request) {
        Timber.d("\ngetFavoriteVacancies() request=%s", request);

        String TABLE = Tables.SearchSites.TABLE_ALL_SITES;

        return db.createQuery(TABLE, "SELECT * FROM " +
                TABLE + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.IS_FAVORITE + " =1") // 1 - favorite vacancy
                .mapToList(VacancyModel.MAPPER);
    }

    @Override
    public Completable removeFromFavorites(VacancyModel vacancy) {
        Timber.d("\nremoveFromFavorites: %s", vacancy);

        return Completable.fromCallable(() -> {
            // updating vacancy in Tables.SearchSites.TABLE_ALL_SITES
            db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                    + " SET " + Tables.SearchSites.Columns.IS_FAVORITE
                    + " =0 WHERE " + Tables.SearchSites.Columns.URL
                    + " ='" + vacancy.url() + "'");

            return true;
        });
    }

    @Override
    public Completable addToFavorite(VacancyModel vacancy) {

        Timber.d("\nSaving to TYPE_FAVORITE table %s", vacancy);
        Cursor cursor = db.query("SELECT * FROM "
                + Tables.SearchSites.TABLE_ALL_SITES
                + " WHERE " + Tables.SearchSites.Columns.IS_FAVORITE + " =1"
                + " AND " + Tables.SearchSites.Columns.URL
                + " = '" + vacancy.url() + "'");

        // don't save vacancy if it already exist in Favorite list
        if (!cursor.moveToFirst()) {
            cursor.close();
            return Completable.fromCallable(() -> {
                // updating vacancy in Tables.SearchSites.TABLE_ALL_SITES
                db.execute("UPDATE " + Tables.SearchSites.TABLE_ALL_SITES
                        + " SET " + Tables.SearchSites.Columns.IS_FAVORITE
                        + " =1 WHERE " + Tables.SearchSites.Columns.URL
                        + " ='" + vacancy.url() + "'");
                return true;
            });
        } else {
            String url = cursor.getString(
                    cursor.getColumnIndex(Tables.SearchSites.Columns.URL));

            Log.e("!!!", "url=" + url);
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
    }

    private void updateRequestTableData(String request) {
        // updating Request's table
        Log.e("VLR", "updating request table");
        db.execute("UPDATE " + Tables.SearchRequest.TABLE_REQUEST
                + " SET " + Tables.SearchRequest.Columns.NEW_VACANCIES
                + "=0 WHERE " + Tables.SearchRequest.Columns.REQUEST
                + "='" + request + "'");
    }

    private boolean shouldBeUpdated(String request) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, 0);
        return preferences.getBoolean(request, false);
    }

    private void saveUpdatingTask(String request, boolean shouldBeUpdated) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, 0);
        preferences.edit().putBoolean(request, shouldBeUpdated).commit();
    }
}
