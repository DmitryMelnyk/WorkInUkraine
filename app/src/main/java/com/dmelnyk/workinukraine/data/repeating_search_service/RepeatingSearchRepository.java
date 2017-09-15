package com.dmelnyk.workinukraine.data.repeating_search_service;

import android.content.Context;
import android.database.Cursor;

import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by d264 on 9/5/17.
 */

public class RepeatingSearchRepository implements IRepeatingSearchRepository {

    private static final String TABLE_REQUEST = Tables.SearchRequest.TABLE_REQUEST;
    private static final String PREF_FILE = "saved_vacancies_count";
    private static final String KEY_PREVIOUS_NEW_VACANCIES_COUNT = "previous_new_vacancies";
    private static final String TABLE_VACANCIES = Tables.SearchSites.TABLE_ALL_SITES;

    private final BriteDatabase db;
    private final Context appContext;

    public RepeatingSearchRepository(BriteDatabase db, Context context) {
        this.db = db;
        appContext = context;
    }

    @Override
    public Single<List<VacancyModel>> getNewVacancies() {

        Observable<List<VacancyModel>> result =
                db.createQuery(TABLE_VACANCIES, "SELECT * FROM " + TABLE_VACANCIES
                + " WHERE " + Tables.SearchSites.Columns.IS_NEW
                + " =1").mapToList(VacancyModel.MAPPER);

        return result.firstOrError();
    }

    @Override
    public int getRequestCount() {
        Cursor cursor = db.query("SELECT * FROM " + TABLE_REQUEST);
        int requestCount = cursor.getCount();
        cursor.close();

        return requestCount;
    }

    @Override
    public int getPreviousNewVacanciesCount() {
        return appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                .getInt(KEY_PREVIOUS_NEW_VACANCIES_COUNT, 0);
    }

    @Override
    public void saveNewVacanciesCount(int newVacancies) {
        appContext.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                .edit().putInt(KEY_PREVIOUS_NEW_VACANCIES_COUNT, newVacancies)
                .commit();
    }

    @Override
    public void close() {
        db.close();
    }
}
