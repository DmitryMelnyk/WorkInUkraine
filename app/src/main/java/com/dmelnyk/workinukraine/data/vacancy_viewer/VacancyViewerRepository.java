package com.dmelnyk.workinukraine.data.vacancy_viewer;

import android.content.ContentValues;
import android.util.Log;

import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewerRepository implements IVacancyViewerRepository {

    private static final String TABLE = Tables.SearchSites.TABLE_ALL_SITES;

    private final BriteDatabase db;

    public VacancyViewerRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Single<List<VacancyModel>> getFavoriteVacancies(String request) {
        Timber.d("getFavoriteVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + Tables.SearchSites.Columns.IS_FAVORITE
                + " =1").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getNewVacancies(String request) {
        Timber.d("getNewVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + Tables.SearchSites.Columns.TIME_STATUS
                + " =1").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getRecentVacancies(String request) {
        Timber.d("getRecentVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + Tables.SearchSites.Columns.TIME_STATUS
                + " =0").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getSiteVacancies(String request, String site) {
        Timber.d("getSiteVacancies " + site);

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + Tables.SearchSites.Columns.SITE
                + " ='" + site + "'").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        Timber.d("updateFavorite. isFavorite=" + !vacancy.isFavorite());

        return Single.fromCallable(() -> {
            ContentValues updatedVacancy = DbItems.createVacancyFavoriteItem(!vacancy.isFavorite(), vacancy);
            db.update(Tables.SearchSites.TABLE_ALL_SITES, updatedVacancy,
                    Tables.SearchSites.Columns.URL + " ='" + vacancy.url() + "'");
            return !vacancy.isFavorite();
        });
    }
}
