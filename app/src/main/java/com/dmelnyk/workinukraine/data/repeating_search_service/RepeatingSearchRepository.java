package com.dmelnyk.workinukraine.data.repeating_search_service;

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

    private static final String TABLE = Tables.SearchSites.TABLE_FAV_NEW_REC;
    private final BriteDatabase db;

    public RepeatingSearchRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Single<List<VacancyModel>> getNewVacancies() {

        Observable<List<VacancyModel>> result = db.createQuery(TABLE, "SELECT * FROM " + TABLE
                + " WHERE " + Tables.SearchSites.Columns.TYPE
                + " ='" + Tables.SearchSites.TYPE_NEW + "'"
        ).mapToList(VacancyModel.MAPPER);

        return result.firstOrError();
    }
}
