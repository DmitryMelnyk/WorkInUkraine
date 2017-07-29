package com.dmelnyk.workinukraine.business.search;

import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.data.RequestModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 6/25/17.
 */

public interface ISearchInteractor {
    /**
     * Returns list of RequestModel items
     * Request format in single string: "request / city"
     * @return The Observable that emits list of search requests
     */
    Observable<List<RequestModel>> getRequests();


    /**
     * Saves request and returns -1 if error happened.
     * @param request The search request in "request / city" format
     */
    Completable saveRequest(String request);

    /**
     * Removes request
     * @param request Te search request in "request / city" format
     */
    void removeRequest(String request);
}
