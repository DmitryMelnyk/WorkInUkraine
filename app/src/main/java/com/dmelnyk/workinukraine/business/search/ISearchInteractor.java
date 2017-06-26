package com.dmelnyk.workinukraine.business.search;

import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;

/**
 * Created by d264 on 6/25/17.
 */

public interface ISearchInteractor {
    SearchRequestModel[] getRequestsData();
}
