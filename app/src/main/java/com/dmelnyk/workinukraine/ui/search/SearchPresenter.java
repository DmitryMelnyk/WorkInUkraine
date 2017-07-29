package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.business.search.ISearchInteractor;
import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchView;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchPresenter implements Contract.ISearchPresenter {

    private final ISearchInteractor interactor;
    private ISearchView view;
    private Disposable disposableRequests;

    public SearchPresenter(ISearchInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void bindView(ISearchView view) {
        this.view = view;
        if (view != null) {
            disposableRequests = interactor.getRequests()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(requestsList ->
                            view.updateData((ArrayList<RequestModel>) requestsList));
        }
    }

    @Override
    public void unbindView() {
        view = null;
        disposableRequests.dispose();
    }

    @Override
    public void addNewRequest(String request) {
        interactor.saveRequest(request).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> { },
                        throwable -> view.showErrorMessage());
    }

    @Override
    public void removeRequest(String request) {
        interactor.removeRequest(request);
    }
}
