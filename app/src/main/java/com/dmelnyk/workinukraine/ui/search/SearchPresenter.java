package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.business.search.ISearchInteractor;
import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
                    .subscribe(requestsList -> {
                        view.updateData((ArrayList<RequestModel>) requestsList);
                        view.updateVacanciesCount(countAllVacancies(requestsList));
                        view.updateNewVacanciesCount(countAllNewVacancies(requestsList));
                    });
        }
    }

    private int countAllNewVacancies(List<RequestModel> requestsList) {
        int allNewVacancies = 0;
        for (RequestModel requestModel : requestsList) {
            allNewVacancies += requestModel.newVacanciesCount();
        }
        return allNewVacancies;
    }

    private int countAllVacancies(List<RequestModel> requestsList) {
        int allVacancies = 0;
        for (RequestModel requestModel : requestsList) {
            allVacancies += requestModel.vacanciesCount();
        }
        return allVacancies;
    }

    @Override
    public void unbindView() {
        view = null;
        disposableRequests.dispose();
    }

    @Override
    public void addNewRequest(String request) {
        interactor.saveRequest(request).observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> { },
                        throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void clearAllRequest() {
        interactor.clearAllRequests()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {},
                        throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void removeRequest(String request) {
        interactor.removeRequest(request);
    }
}
