package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import com.dmelnyk.workinukraine.business.vacancy_viewer.IVacancyViewInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewerPresenter implements Contract.IVacancyViewerPresenter {

    private final IVacancyViewInteractor interactor;
    private Contract.IVacancyViewerView view;
    private Disposable updatingFavoriteDispos;

    public VacancyViewerPresenter(IVacancyViewInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void bindView(Contract.IVacancyViewerView view) {
        this.view = view;
    }

    @Override
    public void unbindView() {
        view = null;
        if (updatingFavoriteDispos != null) {
            updatingFavoriteDispos.dispose();
        }
    }

    @Override
    public void updateFavoriteStatusVacancy(VacancyModel vacancy) {
        updatingFavoriteDispos = interactor.updateFavorite(vacancy).subscribe(
                isFavorite -> view.showUpdatingVacancySuccess(isFavorite),
                throwable -> view.showUpdatingVacancyError());
    }
}
