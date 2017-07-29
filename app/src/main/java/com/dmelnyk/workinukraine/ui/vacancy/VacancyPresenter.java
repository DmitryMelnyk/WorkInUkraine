package com.dmelnyk.workinukraine.ui.vacancy;

import android.util.Log;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyPresenter implements Contract.IVacancyPresenter {

    private final IVacancyInteractor interactor;
    private Contract.IVacancyView view;

    Disposable saveOrRemoveDisposable;
    Disposable vacanciesDisposable;

    public VacancyPresenter(IVacancyInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void bindView(Contract.IVacancyView view, String request, String table) {
        this.view = view;
        if (view != null) {
            // get vacancies
//            vacanciesDisposable = Observable.just(0)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnNext(ignore -> view.displayLoadingProcess())
//                    .observeOn(Schedulers.computation())
//                    .flatMap(ignore -> interactor.getVacancies(request, table))
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnNext(ignore -> view.hideLoadingProcess())
                    interactor.getVacancies(request, table)
                    .subscribe(vacanciesList -> {
                        Log.e("!!!", "vacancie list = " + vacanciesList);
                        view.displayFragment(table, vacanciesList);
                    }, throwable -> {
                        view.showErrorMessage(throwable.getMessage());
                    });
        }
    }

    @Override
    public void unbindView() {
        view = null;
        if (saveOrRemoveDisposable != null) {
            saveOrRemoveDisposable.dispose();
        }
        if (vacanciesDisposable != null) {
            vacanciesDisposable.dispose();
        }
    }

    @Override
    public void onItemPopupMenuClicked(String request, VacancyModel vacancy,
                                       @CardViewAdapter.VacancyPopupMenuType int type) {
        if (type == CardViewAdapter.SHARE) {
            view.createShareIntent(vacancy);
        } else {
            saveOrRemoveDisposable = interactor.onPopupMenuClicked(vacancy, type)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        view.showResultingMessage(type);
                    }, throwable -> {
                        view.showErrorMessage(throwable.getMessage());
                    });
        }
    }
}
