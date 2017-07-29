package com.dmelnyk.workinukraine.model.vacancy;

import android.util.Log;

import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.ui.vacancy.ScrollingActivity;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyRepository implements IVacancyRepository {

    private static final String SELECT_ALL_FROM = "SELECT * FROM ";
    private String currentTable;

    private final BriteDatabase db;

    public VacancyRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Observable<List<VacancyModel>> getVacancies(String request, String table) {
        Timber.d("\ngetVacancies() request=%s from table=%s", request, table);
        currentTable = table;

        String TABLE = "";
        switch (table) {
            case ScrollingActivity.TABLE_FAVORITE:
                TABLE = Tables.SearchSites.FAVORITE;
                break;
            case ScrollingActivity.TABLE_RECENT:
                TABLE = Tables.SearchSites.RECENT;
                break;
            // TODO: other cases
        }

        // TODO:
        return db.createQuery(TABLE, "SELECT * FROM " +
                TABLE + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request + "'")
                .mapToList(VacancyModel.MAPPER);
    }

    @Override
    public Completable removeItem(VacancyModel vacancy) {
        Timber.d("\nremoveItem: %s", vacancy);
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return db.delete(currentTable, Tables.SearchSites.Columns.URL, vacancy.url());
            }
        });
    }

    @Override
    public Completable saveToFavorite(VacancyModel vacancy) {
        Timber.d("\nSaving to FAVORITE table %s", vacancy);
        return Completable.fromCallable(() ->
                db.insert(currentTable, DbItems.createVacancyItem(vacancy))
        );
    }
}
