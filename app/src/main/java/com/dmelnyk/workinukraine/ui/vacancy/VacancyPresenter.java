package com.dmelnyk.workinukraine.ui.vacancy;

import android.util.Log;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyPresenter implements Contract.IVacancyPresenter {

    private final IVacancyInteractor interactor;
    private Contract.IVacancyView view;

    private Disposable saveOrRemoveDisposable;
    private Disposable vacanciesDisposable;
    private Disposable disposableFavorites;

    private static Map<String, Map<String, List<VacancyModel>>> sDataCache;
    private String request;

    public VacancyPresenter(IVacancyInteractor interactor) {
        this.interactor = interactor;
        sDataCache = new HashMap<>();
    }

    @Override
    public void bindView(Contract.IVacancyView view, String request) {
        this.view = view;
        this.request =request;
        if (view == null) return;

        // restoring cached data
//        if (!sDataCache.isEmpty()) {
//            restoreData(sDataCache);
//        }

        getAllVacancies(view, request);
    }

    private void restoreData(Map<String, Map<String, List<VacancyModel>>> mDataCache) {
        view.displayTabFragment(mDataCache);
    }

    private void getAllVacancies(Contract.IVacancyView view, String request) {
        interactor.getAllVacancies(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacanciesMap -> {
                    Log.e("!!! VacancyPr. All = ", vacanciesMap.toString());
                    sDataCache = vacanciesMap;
                    view.displayTabFragment(vacanciesMap);
                }, throwable -> {
                    view.showErrorMessage(throwable.getMessage());
                });
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
    public void onItemPopupMenuClicked(VacancyModel vacancy,
                                       @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        if (type == VacancyCardViewAdapter.MENU_SHARE) {
            view.createShareIntent(vacancy);
        } else {
            saveOrRemoveDisposable = interactor.onPopupMenuClicked(vacancy, type)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        view.showResultingMessage(type);
                        updateFavorite();
                    }, throwable -> {
                        view.showErrorMessage(throwable.getMessage());
                    });
        }
    }

    private void updateFavorite() {
        disposableFavorites = interactor.getVacancies(request, IVacancyInteractor.VACANCIES_FAVORITE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacancies -> view.updateFavoriteTab(vacancies),
                        throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void clear() {
        sDataCache = null;
    }
}
