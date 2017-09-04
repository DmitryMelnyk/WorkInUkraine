package com.dmelnyk.workinukraine.data.search;

import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchRepository implements ISearchRepository {

    private static final String REQUEST_TABLE = Tables.SearchRequest.TABLE_REQUEST;
    private final BriteDatabase db;

    public SearchRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Completable clearAllRequests() {
        Log.e("555", "Clearing all data");
        try {
            db.delete(Tables.SearchRequest.TABLE_REQUEST, "");
            db.delete(Tables.SearchSites.TABLE_ALL_SITES, "");
            db.delete(Tables.SearchSites.TABLE_FAV_NEW_REC, "");
        } catch (Exception e) {
            Log.e("555", e.getMessage());
            return Completable.error(e);
        }

        return Completable.complete();
    }

    @Override
    public Observable<List<RequestModel>> loadRequestList() {
        Timber.d("\nloadRequestList()");
        return db.createQuery(REQUEST_TABLE, "SELECT * FROM " + REQUEST_TABLE)
                .mapToList(RequestModel.MAPPER);
    }

    @Override
    public void removeDataFromTables(String request) {
        Timber.d("\nRemoving data with request=%s from all tables", request);
        db.delete(Tables.SearchSites.TABLE_ALL_SITES, Tables.SearchSites.Columns.REQUEST + " = '" + request +"'");
        db.delete(Tables.SearchSites.TABLE_FAV_NEW_REC, Tables.SearchSites.Columns.REQUEST + " = '" + request +"'");
    }

    @Override
    public void removeRequest(@NonNull String request) {
        Timber.d("\nremoveRequest: " + request);
        db.delete(REQUEST_TABLE, where(request));
    }

    @Override
    public Completable saveRequest(String request) {
        Timber.d("\nsaveRequest: " + request);
        return Completable.fromCallable(() ->
            db.insert(REQUEST_TABLE, DbItems.createRequestItem(request, 0, 0, -1l)));
    }

    @Override
    public void updateRequest(@NonNull String oldRequest, String newRequest) {
        Timber.d("\nupdateRequest");
        removeRequest(oldRequest);
        saveRequest(newRequest);
    }

    private String where(String request) {
        return Tables.SearchRequest.Columns.REQUEST + " = '" + request + "'";
    }
}
