package com.dmelnyk.workinukraine.ui.search;

import android.util.Log;

import com.dmelnyk.workinukraine.ui.search.business.ISearchInteractor;
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
        boolean isConnected = view.getInternetStatus();
        // hide/show connection status
        updateInternetStatusView(isConnected);
    }

    @Override
    public void addRequest(String request) {
        interactor.saveRequest(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> { },
                        throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void editRequest(String previousRequest, String newRequest) {
        interactor.editRequest(previousRequest, newRequest)
                .subscribe(
                        () -> { /* NOP */},
                        throwable -> view.showErrorMessage(null)
                );
    }

    @Override
    public void unbindView() {
        view = null;
        disposableRequests.dispose();
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
    public void onInternetStatusChanged(boolean isConnected) {
        updateInternetStatusView(isConnected);
    }

    private void updateInternetStatusView(boolean isConnected) {
        if (isConnected) {
            view.hideNoConnection();
        } else {
            view.showNoConnection();
        }
    }

    @Override
    public void removeRequest(String request) {
        interactor.removeRequest(request);
    }

    @Override
    public void updateData() {
        getRequests();
    }

    private void getRequests() {
        disposableRequests = interactor.getRequests()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(requestsList -> {
                    if (view != null) {
                        displayData(requestsList);
                    }
                });
    }

    private void displayData(List<RequestModel> requestsList) {
        view.updateData((ArrayList<RequestModel>) requestsList);
        view.updateVacanciesCount(countAllVacancies(requestsList));
        view.updateNewVacanciesCount(countAllNewVacancies(requestsList));
        updateLastUpdateTime(requestsList);
    }

    private void updateLastUpdateTime(List<RequestModel> requestsList) {
        if (requestsList.isEmpty()) {
            // show empty time
            view.updateLastSearchTime("");
            return;
        } else {
            for (RequestModel request: requestsList) {
                long time = request.updated();
                if (time == -1l) {
                    continue;
                }

                // Update time with firs non default (-1l) value
                // -1l means that there was no search processing yet.
                SimpleDateFormat timeFormat = new SimpleDateFormat("EE, HH:mm", Locale.getDefault());
                String updated = timeFormat.format(new Date(time));
                view.updateLastSearchTime(updated);

                return;
            }
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
