package com.dmelnyk.workinukraine.business.search;

import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.model.search.ISearchRepository;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchInteractor implements ISearchInteractor {
    public SearchInteractor(ISearchRepository repository) {
        super();
    }

    @Override
    public SearchRequestModel[] getRequestsData() {
        SearchRequestModel[] models = new SearchRequestModel[4];
        SearchRequestModel item = SearchRequestModel.Builder()
                .withAvatar(null)
                .withRequest("android development")
                .withCity("Киев")
                .withJobCount(75)
                .withLastUpdate("17:34")
                .build();
        for (int i = 0; i < 4; i++) {
            models[i] = item;
        }

        return models;
    }
}
