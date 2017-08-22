package com.dmelnyk.workinukraine.ui.vacancy;

import android.util.Log;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

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
    private static String sError;
    private static boolean sIsDisplayed;
    private static String mRequest;
    private static List<VacancyModel> sFavoriteVacanciesCache;

    public VacancyPresenter(IVacancyInteractor interactor, String request) {
        this.interactor = interactor;
        this.mRequest = request;
        // Don't get data from db if we have saved data
        if (sDataCache == null) {
            getAllVacancies(request);
        }
    }

    @Override
    public void bindView(Contract.IVacancyView view, String request) {
        this.view = view;

        // Don't do anything after orientation changes return
        Log.e("!!!", "sIsDislpayed=" + sIsDisplayed);
        if (sIsDisplayed) return;

        // If request to database has been already received get the result in cache
        if (sDataCache != null) {
            // successful result
            Log.e("!!!", "VacancyPresenter. sDataCache=" + sDataCache);
            view.displayTabFragment(sDataCache);
        } else if (sError != null) {
            // error result
            view.showErrorMessage(sError);
            sError = null;
        }
    }

    private void getAllVacancies(String request) {
        interactor.getAllVacancies(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacanciesMap -> {
                    Log.e("!!! VacancyPr. All = ", vacanciesMap.toString());
                    sDataCache = new HashMap<>(vacanciesMap);
                    if (view != null) {
                        sIsDisplayed = true;
                        view.displayTabFragment(vacanciesMap);
                    }
                }, throwable -> {
                    Timber.e(throwable.getStackTrace().toString());
                    if (view != null) {
                        view.showErrorMessage(throwable.getMessage());
                    } else {
                        // if view hasn't been initialized yet save error msg to cache
                        sError = throwable.getMessage();
                    }
                });
    }

    @Override
    public void unbindView() {
        view = null;
        sIsDisplayed = false;
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
                        updateFavorite();
                        view.showResultingMessage(type);
                    }, throwable -> {
                        view.showErrorMessage(throwable.getMessage());
                    });
        }
    }

    private void updateFavorite() {
        disposableFavorites = interactor.getFavoriteVacancies(mRequest, IVacancyInteractor.VACANCIES_FAVORITE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacancies -> {
                    view.updateFavoriteTab(vacancies);
                    // TODO: save type
//                    view.showResultingMessage(type);
                    sFavoriteVacanciesCache = new ArrayList<VacancyModel>(vacancies);
                }, throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    @Override
    public void clear() {
        sDataCache = null;
        sFavoriteVacanciesCache = null;
        sError = null;
        sIsDisplayed = false;
    }
}
