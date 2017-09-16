package com.dmelnyk.workinukraine.data.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.models.RequestModel;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 6/25/17.
 */

public interface ISearchRepository {

    /**
     * Restores saved in database search requests
     * Request format in single string: "request / city"
     * @return The Observable that emits list of search requests
     */
    @Nullable Observable<List<RequestModel>> loadRequestList();

    /**
     * Removes request from database
     * @param request
     */
    void removeRequest(@NonNull String request);

    /**
     * Saves new request in database
     * Request format in single string: "request / city"
     * @param request The new request.
     */
    Completable addRequest(@NonNull String request);

    /**
     * Removes oldRequest from database and adds newRequest to database
     * @param oldRequest The request that will be removed from database
     * @param newRequest The request that will be added to database
     */
    void updateRequest(@NonNull String oldRequest, String newRequest);

    /**
     * Removes all requests and vacancies from request's and vacancies's tables
     * @return
     */
    Completable clearAllRequests();
}
