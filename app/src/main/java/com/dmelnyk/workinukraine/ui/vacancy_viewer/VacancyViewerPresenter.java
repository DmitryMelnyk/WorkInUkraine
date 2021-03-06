package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.support.annotation.Nullable;
import android.util.Log;

import com.dmelnyk.workinukraine.ui.vacancy_viewer.business.IVacancyViewInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewerPresenter implements Contract.IVacancyViewerPresenter {

    private final IVacancyViewInteractor interactor;
    private Contract.IVacancyViewerView view;
    private Disposable updatingFavoriteDispos;
    private Disposable vacanciesDisposable;

    public VacancyViewerPresenter(IVacancyViewInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void onCreate(Contract.IVacancyViewerView view) {
        this.view = view;
    }

    @Override
    public void onResume() {
        boolean isConnected = view.isConnected();
        // hide/show connection status
        updateInternetStatusView(isConnected);
    }

    @Override
    public void onStop() {
        if (updatingFavoriteDispos != null) {
            updatingFavoriteDispos.dispose();
            vacanciesDisposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        interactor.clear();
        view = null;
    }

    @Override
    public void getData(String request, String type, @Nullable String site) {
        vacanciesDisposable = interactor.getVacancies(request, type, site)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacancies -> {
                    if (view != null) {
                       view.displayVacancies(vacancies);
                    }
                }, throwable -> view.showUpdatingVacancyError());

    }

    @Override
    public void updateFavoriteStatusVacancy(VacancyModel vacancy) {
        updatingFavoriteDispos = interactor.updateFavorite(vacancy).subscribe(
                isFavorite -> view.showUpdatingVacancySuccess(isFavorite),
                throwable -> view.showUpdatingVacancyError());
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

}
