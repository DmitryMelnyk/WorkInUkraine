package com.dmelnyk.workinukraine.business.search;

import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.model.search.ISearchRepository;

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
        return repository.clearAllRequests();
    }

    @Override
    public Observable<List<RequestModel>> getRequests() {
        return repository.loadRequestList();
    }

    @Override
    public void removeRequest(String request) {
        repository.removeRequest(request);
        repository.removeDataFromTables(request);
    }

    @Override
    public Completable saveRequest(String request) {
        return repository.saveRequest(request);
    }


}
