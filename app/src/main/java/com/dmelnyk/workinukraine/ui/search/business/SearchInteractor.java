package com.dmelnyk.workinukraine.ui.search.business;

import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.ui.search.repository.ISearchRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchInteractor implements ISearchInteractor {
    ISearchRepository repository;

    public SearchInteractor(ISearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Completable clearAllRequests() {
        repository.clearAllSharedPrefData();
        return repository.clearAllRequests();
    }

    @Override
    public Observable<List<RequestModel>> getRequests() {
        return repository.loadRequestList();
    }

    @Override
    public void removeRequest(String request) {
        repository.removeRequest(request);
        repository.clearAllSharedPrefData();
    }

    @Override
    public Completable saveRequest(String request) {
        return repository.addRequest(request);
    }

    @Override
    public Completable editRequest(String previousRequest, String newRequest) {
//        repository.clearAllSharedPrefData();
        return repository.updateRequest(previousRequest, newRequest);
    }

}
