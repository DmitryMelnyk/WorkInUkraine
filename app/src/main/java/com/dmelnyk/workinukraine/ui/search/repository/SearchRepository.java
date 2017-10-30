package com.dmelnyk.workinukraine.ui.search.repository;

import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.utils.SharedPrefFilterUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.DbItems;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchRepository implements ISearchRepository {

    private static final String REQUEST_TABLE = DbContract.SearchRequest.TABLE_REQUEST;
    private static final String VACANCY_TABLE = DbContract.SearchSites.TABLE_ALL_SITES;
    private final BriteDatabase db;

    private final SharedPrefUtil sharedPrefUtil;
    private final SharedPrefFilterUtil filterUtil;

    public SearchRepository(BriteDatabase db, SharedPrefUtil sharedPrefUtil, SharedPrefFilterUtil filterUtil) {
        this.db = db;
        this.sharedPrefUtil = sharedPrefUtil;
        this.filterUtil = filterUtil;
    }

    @Override
    public Completable clearAllRequests() {
        try {
            db.delete(DbContract.SearchRequest.TABLE_REQUEST, "");
            db.delete(DbContract.SearchSites.TABLE_ALL_SITES, "");
        } catch (Exception e) {
            Timber.e(e.getMessage());
            return Completable.error(e);
        }

        return Completable.complete();
    }

    @Override
    public void clearAllSharedPrefData() {
        sharedPrefUtil.clearData();
        filterUtil.clearAllFilters();
    }

    @Override
    public void clearFiltersForRequest(String request) {
        filterUtil.clearFiltersForRequest(request);
    }

    @Override
    public Observable<List<RequestModel>> loadRequestList() {
        Timber.d("\nloadRequestList()");
        return db.createQuery(REQUEST_TABLE, "SELECT * FROM " + REQUEST_TABLE)
                .mapToList(RequestModel.MAPPER);
    }

    @Override
    public void removeRequest(@NonNull String request) {
        Timber.d("\nremoveRequest: " + request);
        db.delete(REQUEST_TABLE, where(request));
        db.delete(VACANCY_TABLE, where(request));
    }

    @Override
    public Completable addRequest(String request) {
        Timber.d("\naddRequest: " + request);
        return Completable.fromCallable(() ->
            db.insert(REQUEST_TABLE, DbItems.createRequestItem(request, 0, 0, -1l)));
    }

    @Override
    public Completable updateRequest(@NonNull String previousRequest, String newRequest) {
        Timber.d("\nupdateRequest");

        // edits  request
        ContentValues newItem = DbItems.createRequestItem(newRequest, 0, 0, -1l);
        try {
            db.update(DbContract.SearchRequest.TABLE_REQUEST, newItem,
                    DbContract.SearchRequest.Columns.REQUEST + " ='" + previousRequest + "'");

            clearRequestRelatedData(previousRequest);
            return Completable.complete();
            // if request already exist in db throws exception
        } catch (SQLiteConstraintException exception) {
            return Completable.error(new Throwable("Error"));
        }
    }

    private void clearRequestRelatedData(@NonNull String request) {
        // removes previous request's vacancy
        db.delete(VACANCY_TABLE, DbContract.SearchSites.Columns.REQUEST + "='"+ request + "'");
        // removes filter words
//        filterUtil.clearFiltersForRequest(request);
    }

    private String where(String request) {
        return DbContract.SearchRequest.Columns.REQUEST + " = '" + request + "'";
    }
}
