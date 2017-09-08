package com.dmelnyk.workinukraine.ui.search;

import android.util.Log;

import com.dmelnyk.workinukraine.business.search.ISearchInteractor;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.ui.search.Contract.ISearchView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        getRequests();
    }


    @Override
    public void unbindView() {
        view = null;
        disposableRequests.dispose();
    }

    @Override
    public void addRequest(String request) {
        interactor.saveRequest(request)
                .observeOn(AndroidSchedulers.mainThread())
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
    public void getFreshRequests() {
        getRequests();
    }

    private void getRequests() {
        disposableRequests = interactor.getRequests()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestsList -> {
                    Log.e("999", "all vacancies count=" + countAllVacancies(requestsList));
                    view.updateData((ArrayList<RequestModel>) requestsList);
                    view.updateVacanciesCount(countAllVacancies(requestsList));
                    view.updateNewVacanciesCount(countAllNewVacancies(requestsList));

                    // TODO: fix adding request in a first time if (requestsList.get(0).updated())
                    updateLastUpdateTime(requestsList);
                });
    }

    @Override
    public void removeRequest(String request) {
        interactor.removeRequest(request);
    }

    @Override
    public void updateData() {
        getRequests();
    }

    private void updateLastUpdateTime(List<RequestModel> requestsList) {
        if (requestsList.isEmpty()) {

        } else {
            long time = requestsList.get(0).updated();
            SimpleDateFormat timeFormat = new SimpleDateFormat("EE, HH:mm", Locale.getDefault());
            String updated = timeFormat.format(new Date(time));
            Log.e("1010", "updated time=" + updated);
            view.updateLastSearchTime(updated);
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
}
