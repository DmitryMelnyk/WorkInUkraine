package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.business.search.ISearchInteractor;
import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchView;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchPresenter implements Contract.ISearchPresenter {


    private final ISearchInteractor interactor;
    private ISearchView view;

    public SearchPresenter(ISearchInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void bindView(ISearchView view) {
        this.view = view;
        if (view != null) {
            SearchRequestModel[] data = interactor.getRequestsData();
            view.updateData(data);
        }
    }

    @Override
    public void unbindView() {
        view = null;
    }
}
