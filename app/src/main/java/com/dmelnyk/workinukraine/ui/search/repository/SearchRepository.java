package com.dmelnyk.workinukraine.ui.search.data;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.data.SharedPrefUtil;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.DbItems;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import javax.inject.Inject;

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

    @Inject
    SharedPrefUtil sharedPrefUtil;

    public SearchRepository(BriteDatabase db) {
        this.db = db;
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
    public void updateRequest(@NonNull String oldRequest, String newRequest) {
        Timber.d("\nupdateRequest");

        Log.e("@@", "old==new =" + oldRequest.equals(newRequest));
        if (oldRequest.equals(newRequest)) return;

        // edits  request
        ContentValues newItem = DbItems.createRequestItem(newRequest, 0, 0, -1l);
        db.update(DbContract.SearchRequest.TABLE_REQUEST, newItem, DbContract.SearchRequest.Columns.REQUEST
                + " ='" + oldRequest + "'");

        // removes previous request's vacancy
        db.delete(VACANCY_TABLE, oldRequest);
    }

    private String where(String request) {
        return DbContract.SearchRequest.Columns.REQUEST + " = '" + request + "'";
    }
}
