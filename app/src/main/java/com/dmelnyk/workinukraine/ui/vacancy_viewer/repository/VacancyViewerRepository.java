package com.dmelnyk.workinukraine.ui.vacancy_viewer.repository;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Single;
import timber.log.Timber;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewerRepository implements IVacancyViewerRepository {

    private static final String TABLE = DbContract.SearchSites.TABLE_ALL_SITES;

    private final BriteDatabase db;

    public VacancyViewerRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public Single<List<VacancyModel>> getFavoriteVacancies(String request) {
        Timber.d("getFavoriteVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + DbContract.SearchSites.Columns.IS_FAVORITE
                + " =1").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getNewVacancies(String request) {
        Timber.d("getNewVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS
                + " =1").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getRecentVacancies(String request) {
        Timber.d("getRecentVacancies");

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + DbContract.SearchSites.Columns.TIME_STATUS
                + " =0").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<List<VacancyModel>> getSiteVacancies(String request, String site) {
        Timber.d("getSiteVacancies " + site);

        return db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + DbContract.SearchSites.Columns.REQUEST
                + " ='" + request + "' AND " + DbContract.SearchSites.Columns.SITE
                + " ='" + site + "'").mapToList(VacancyModel.MAPPER).elementAtOrError(0);
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        Timber.d("updateFavorite. isFavorite=" + !vacancy.isFavorite());

        return Single.fromCallable(() -> {
            ContentValues updatedVacancy = DbItems.createVacancyFavoriteItem(!vacancy.isFavorite(), vacancy);
            db.update(DbContract.SearchSites.TABLE_ALL_SITES, updatedVacancy,
                    DbContract.SearchSites.Columns.URL + " ='" + vacancy.url() + "'");
            return !vacancy.isFavorite();
        });
    }
}