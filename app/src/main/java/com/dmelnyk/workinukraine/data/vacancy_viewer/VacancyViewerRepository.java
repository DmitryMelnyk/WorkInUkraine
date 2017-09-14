package com.dmelnyk.workinukraine.data.vacancy_viewer;

import android.content.ContentValues;

import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import io.reactivex.Single;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewerRepository implements IVacancyViewerRepository {

    private final BriteDatabase db;

    public VacancyViewerRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        return Single.fromCallable(() -> {
            ContentValues updatedVacancy = DbItems.createVacancyItem(!vacancy.isFavorite(), vacancy);
            db.update(Tables.SearchSites.TABLE_ALL_SITES, updatedVacancy,
                    Tables.SearchSites.Columns.URL + " ='" + vacancy.url() + "'");
            return !vacancy.isFavorite();
        });
    }
}
