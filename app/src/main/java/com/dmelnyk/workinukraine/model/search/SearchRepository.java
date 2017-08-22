package com.dmelnyk.workinukraine.model.search;

import android.support.annotation.NonNull;

import com.dmelnyk.workinukraine.data.RequestModel;
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
    private final BriteDatabase bd;

    public SearchRepository(BriteDatabase bd) {
        this.bd = bd;
    }

    @Override
    public Observable<List<RequestModel>> loadRequestList() {
        Timber.d("\nloadRequestList()");
        return bd.createQuery(REQUEST_TABLE, "SELECT * FROM " + REQUEST_TABLE)
                .mapToList(RequestModel.MAPPER);
    }

    @Override
    public void removeDataFromTables(String request) {
        Timber.d("\nRemoving data with request=%s from all tables", request);
        bd.delete(Tables.SearchSites.TABLE_ALL_SITES, Tables.SearchSites.Columns.REQUEST + " = '" + request +"'");
        bd.delete(Tables.SearchSites.TABLE_FAV_NEW_REC, Tables.SearchSites.Columns.REQUEST + " = '" + request +"'");
    }

    @Override
    public void removeRequest(@NonNull String request) {
        Timber.d("\nremoveRequest: " + request);
        bd.delete(REQUEST_TABLE, where(request));
    }

    @Override
    public Completable saveRequest(String request) {
        Timber.d("\nsaveRequest: " + request);
        return Completable.fromCallable(() ->
            bd.insert(REQUEST_TABLE, DbItems.createRequestItem(request, 0, -1l)));
    }

    @Override
    // TODO: add vacancies count and time of last update
    public void updateRequest(@NonNull String oldRequest, String newRequest) {
        Timber.d("\nupdateRequest");
        removeRequest(oldRequest);
        saveRequest(newRequest);
    }
    private String where(String request) {
        return Tables.SearchRequest.Columns.REQUEST + " = '" + request + "'";
    }
}
