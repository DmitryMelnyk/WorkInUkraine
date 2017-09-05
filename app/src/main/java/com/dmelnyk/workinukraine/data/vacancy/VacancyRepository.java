package com.dmelnyk.workinukraine.data.vacancy;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.models.VacancyContainer;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.db.Db;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyRepository implements IVacancyRepository {

    private final BriteDatabase db;
    private final Context context;

    public VacancyRepository(BriteDatabase db, Context context) {
        this.db = db;
        this.context = context;
    }

    @Override
    public Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request) {
        Timber.d("\ngetAllVacancies, request =%s", request);

        // Get new vacancies and then clear them after displaying first time
        List<VacancyModel> newVacancies = getNewVacancies(request);

        // Vacancies from TABLE_ALL_SITES
        Observable<List<VacancyContainer>> sitesObservable =
                db.createQuery(Tables.SearchSites.TABLE_ALL_SITES, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_ALL_SITES + " WHERE " +
                        Tables.SearchSites.Columns.REQUEST + " ='" + request + "'")
                        .mapToList(VacancyContainer.MAPPER);

        // Vacancies from TABLE_ALL_SITES
        Observable<List<VacancyContainer>> favoriteNewRecentObservable =
                db.createQuery(Tables.SearchSites.TABLE_FAV_NEW_REC, "SELECT * FROM "
                        + Tables.SearchSites.TABLE_FAV_NEW_REC + " WHERE " +
                        Tables.SearchSites.Columns.REQUEST + " ='" + request + "'")
                        .mapToList(VacancyContainer.MAPPER);

        if (!newVacancies.isEmpty()) convertNewToRecentVacancies(request);
        db.close();
        return Observable.zip(favoriteNewRecentObservable, sitesObservable, (favorite, sites) -> {
            Map<String, Map<String, List<VacancyModel>>> result =
                    new HashMap<>();

            result.put(IVacancyInteractor.DATA_TAB_SITES, convertToSiteMap(sites));
            result.put(IVacancyInteractor.DATA_OTHER_TABS, convertToFNRMap(favorite, newVacancies));
            return result;
        });
    }

    private List<VacancyModel> getNewVacancies(String request) {
        List<VacancyModel> vacancies = new ArrayList<>();

        Cursor cursor = db.query("SELECT * FROM "
                + Tables.SearchSites.TABLE_FAV_NEW_REC
                + " WHERE " + Tables.SearchSites.Columns.REQUEST
                + " = '" + request + "' AND " + Tables.SearchSites.Columns.TYPE
                + " = '" + Tables.SearchSites.TYPE_NEW + "'");

        if (cursor.moveToFirst()) {
            do {
                String title = Db.getString(cursor, Tables.SearchSites.Columns.TITLE);
                String date = Db.getString(cursor, Tables.SearchSites.Columns.DATE);
                String url = Db.getString(cursor, Tables.SearchSites.Columns.URL);

                VacancyModel vacancy = VacancyModel.builder()
                        .setRequest(request)
                        .setTitle(title)
                        .setDate(date)
                        .setUrl(url)
                        .build();
                vacancies.add(vacancy);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return vacancies;
    }

    private void convertNewToRecentVacancies(String request) {
        Timber.d("\nclearing New vacancies with request=%s", request);

        db.execute("UPDATE " + Tables.SearchSites.TABLE_FAV_NEW_REC
                + " SET " + Tables.SearchSites.Columns.TYPE + "='"
                + Tables.SearchSites.TYPE_RECENT + "' WHERE "
                + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.TYPE + "='"
                + Tables.SearchSites.TYPE_NEW + "'"
        );

        // updating Request's table
        db.execute("UPDATE " + Tables.SearchRequest.TABLE_REQUEST
                + " SET " + Tables.SearchRequest.Columns.NEW_VACANCIES
                + "='0' WHERE " + Tables.SearchRequest.Columns.REQUEST
                + "='" + request + "'");
    }

    private Map<String, List<VacancyModel>> convertToFNRMap(
            List<VacancyContainer> vacancyContainerList, List<VacancyModel> newVacancies) {

        Map<String, List<VacancyModel>> result = new HashMap<>();
        List<VacancyModel>[] vacancyList = new List[3];
        for (int i = 0; i < vacancyList.length; i++) {
            vacancyList[i] = new ArrayList<>();
        }

        for (VacancyContainer container : vacancyContainerList) {
            if (container.getType().equals(Tables.SearchSites.TYPE_FAVORITE)) {
                vacancyList[0].add(container.getVacancy());
            } else if (container.getType().equals(Tables.SearchSites.TYPE_NEW)) {
                vacancyList[1].add(container.getVacancy());
            } else if (container.getType().equals(Tables.SearchSites.TYPE_RECENT)) {
                vacancyList[2].add(container.getVacancy());
            }
        }

        result.put(Tables.SearchSites.TYPE_FAVORITE, vacancyList[0]);
        result.put(Tables.SearchSites.TYPE_NEW, newVacancies);
        result.put(Tables.SearchSites.TYPE_RECENT, vacancyList[2]);

        return result;
    }

    private Map<String, List<VacancyModel>> convertToSiteMap(
            List<VacancyContainer> vacancyContainerList) {

        Map<String, List<VacancyModel>> sites = new LinkedHashMap<>();
        List<VacancyContainer>[] siteList = new List[5];
        for (int i = 0; i < siteList.length; i++) {
            siteList[i] = new ArrayList<>();
        }

        for (VacancyContainer container : vacancyContainerList) {
            for (int i = 0; i < Tables.SearchSites.TYPE_SITES.length; i++) {
                if (container.getType().equals(Tables.SearchSites.TYPE_SITES[i])) {
                    siteList[i].add(container);
                break;
                }
            }
        }

        // Sorts lists in descending order
        sortCollections(siteList);

        for (int i = 0; i < siteList.length; i++) {
            if (siteList[i] != null && !siteList[i].isEmpty())
                sites.put(siteList[i].get(0).getType(), convertToVacancyModel(siteList[i]));
        }

        return sites;

    }

    private void sortCollections(List<VacancyContainer>[] siteList) {
        Arrays.sort(siteList, new Comparator<List<VacancyContainer>>() {
            @Override
            public int compare(List<VacancyContainer> l1, List<VacancyContainer> l2) {
                return l2.size() - l1.size();
            }
        });
    }

    protected List<VacancyModel> convertToVacancyModel(List<VacancyContainer> vacancyContainerList) {
        List<VacancyModel> vacancies = new ArrayList<>();
        for (VacancyContainer vacancyContainer : vacancyContainerList) {
            vacancies.add(vacancyContainer.getVacancy());
        }

        return vacancies;
    }

    @Override
    public Observable<List<VacancyModel>> getFavoriteVacancies(String request, String table) {
        Timber.d("\ngetFavoriteVacancies() request=%s from table=%s", request, table);

        String TABLE = Tables.SearchSites.TABLE_FAV_NEW_REC;

        return db.createQuery(TABLE, "SELECT * FROM " +
                TABLE + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request
                + "' AND " + Tables.SearchSites.Columns.TYPE + " = '"
                + Tables.SearchSites.TYPE_FAVORITE + "'")
                .mapToList(VacancyModel.MAPPER);
    }

    @Override
    public Completable removeFromFavorites(VacancyModel vacancy) {
        Timber.d("\nremoveFromFavorites: %s", vacancy);
        return Completable.fromCallable(() ->
                db.delete(Tables.SearchSites.TABLE_FAV_NEW_REC,
                        Tables.SearchSites.Columns.URL + " ='" + vacancy.url()
                                + "' AND " + Tables.SearchSites.Columns.TYPE
                                + " ='" + Tables.SearchSites.TYPE_FAVORITE + "'"
                )
        );
    }

    @Override
    public Completable saveToFavorite(VacancyModel vacancy) {
        Timber.d("\nSaving to TYPE_FAVORITE table %s", vacancy);
        Cursor cursor = db.query("SELECT * FROM "
                + Tables.SearchSites.TABLE_FAV_NEW_REC
                + " WHERE " + Tables.SearchSites.Columns.TYPE + " = '"
                + Tables.SearchSites.TYPE_FAVORITE
                + "' AND " + Tables.SearchSites.Columns.URL
                + " = '" + vacancy.url() + "'");

        // don't save vacancy if it already exist in Favorite list
        if (!cursor.moveToFirst()) {
            cursor.close();
            return Completable.fromCallable(() ->
                    db.insert(Tables.SearchSites.TABLE_FAV_NEW_REC,
                            DbItems.createVacancyContainerFavNewRecItem(
                                    Tables.SearchSites.TYPE_FAVORITE,
                                    vacancy))
            );
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
    public String[] getRecentTitles() {
        return context.getResources().getStringArray(R.array.tab_titles_with_recent);
    }
}
