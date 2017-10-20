package com.dmelnyk.workinukraine.ui.vacancy_list;

import android.util.Log;
import android.util.Pair;

import com.dmelnyk.workinukraine.ui.vacancy_list.business.IVacancyListInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.dmelnyk.workinukraine.ui.vacancy_list.business.IVacancyListInteractor.DATA_FAVORITE;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyListPresenter implements Contract.IVacancyPresenter {

    private final IVacancyListInteractor interactor;
    private Contract.IVacancyView view;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static Map<String, List<VacancyModel>> sDataCache;
    private static boolean sIsDisplayed;
    private static String mRequest;
    private Disposable favoritesDisposable;

    public VacancyListPresenter(IVacancyListInteractor interactor, String request) {
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
        if (sIsDisplayed) return;

        // If request to database has been already received get the result in cache
        if (sDataCache != null) {
            // successful result
            displayData(sDataCache);
        }
    }

    @Override
    public void onResume(Contract.IVacancyView view) {
        this.view = view;
        if (compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        updateFavorites();
    }

    @Override
    public void onStop() {
        view = null;
        sIsDisplayed = false;
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }

        if (favoritesDisposable != null) {
            favoritesDisposable.dispose();
        }
    }
    @Override
    public Pair<Boolean, Set<String>> getFilterData() {
        return interactor.getFilterData();
    }

    @Override
    public void filterUpdated(Pair<Boolean, Set<String>> data) {
        compositeDisposable.add(
                interactor.updateFilter(data)
                .subscribe(() -> {/* NOP */}));

        getAllVacancies(mRequest);
    }

    private void displayData(Map<String, List<VacancyModel>> vacanciesMap) {
        // Counting vacancies count in all tabs
        int siteTabsCount = vacanciesMap.get(IVacancyListInteractor.DATA_ALL).size();
        int newVacanciesCount = vacanciesMap.get(IVacancyListInteractor.DATA_NEW).size();
        int recentVacanciesCount = vacanciesMap.get(IVacancyListInteractor.DATA_RECENT).size();
        int favoriteVacanciesCount = vacanciesMap.get(DATA_FAVORITE).size();

        // Exit from activity if no vacancies found
        if (siteTabsCount == 0) {
            view.exitActivity();
            clear();
            return;
        }

        int[] tabVacancyCount = newVacanciesCount > 0 && recentVacanciesCount > 0
                ? new int[4] : new int[3];

        String[] tabTitles = null;
        int buttonTabType = 0; // 1 - only new vacancies, 2 - new and recent, 3 - only recent

        tabVacancyCount[0] = siteTabsCount; // first item is always the same
        if (newVacanciesCount > 0 && recentVacanciesCount == 0) {
            tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_NEW);
            tabVacancyCount[1] = newVacanciesCount;
            tabVacancyCount[2] = favoriteVacanciesCount;
            buttonTabType = 1;
        }

        if (newVacanciesCount > 0 && recentVacanciesCount > 0) {
            tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_NEW_AND_RECENT);
            tabVacancyCount[1] = newVacanciesCount;
            tabVacancyCount[2] = recentVacanciesCount;
            tabVacancyCount[3] = favoriteVacanciesCount;
            buttonTabType = 2;
        }

        if (newVacanciesCount == 0) {
            tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_RECENT);
            tabVacancyCount[1] = recentVacanciesCount;
            tabVacancyCount[2] = favoriteVacanciesCount;
            buttonTabType = 3;
        }

        view.displayTabFragment(tabTitles, tabVacancyCount, buttonTabType, vacanciesMap);
    }

    private void getAllVacancies(String request) {
        compositeDisposable.add(
                interactor.getAllVacancies(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacanciesMap -> {
                    sDataCache = new HashMap<>(vacanciesMap);
                    if (view != null) {
                        sIsDisplayed = true;
                        displayData(sDataCache);
                    }
                }));
    }

    @Override
    public void updateVacanciesTimeStatus() {
        interactor.onVacanciesViewed(mRequest);
    }

    @Override
    public void onItemPopupMenuClicked(VacancyModel vacancy,
                                       @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        if (type == VacancyCardViewAdapter.MENU_SHARE) {
            view.createShareIntent(vacancy);
        } else {
            compositeDisposable.add(
                    interactor.onPopupMenuClicked(vacancy, type)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        view.showResultingMessage(type);
                    }, throwable -> {
                        view.showAddToFavoriteErrorMessage();
                    }));
        }
    }

    // This method will update favorites after adding/removing vacancies in proper db automatically
    private void updateFavorites() {
        Log.d(getClass().getSimpleName(), "updateFavorites is called. Request = " + mRequest);

        if (favoritesDisposable != null) {
            favoritesDisposable.dispose();
        } else {
            favoritesDisposable = interactor.getFavoriteVacancies(mRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(favorites -> {
                        Log.d(getClass().getSimpleName(), "updateFavorites is called. Favorites=" + favorites);
                        // updates cached data
                        if (sDataCache != null) {
                            sDataCache.put(DATA_FAVORITE, favorites);
                        } else {
                            Log.e(getClass().getSimpleName(), "sDataCache=null");
                        }
                        view.updateFavoriteTab(favorites);
                    }, throwable -> {
                        Log.e(getClass().getSimpleName(), throwable.getMessage());
                        view.showAddToFavoriteErrorMessage();
                    });

            boolean addResult = compositeDisposable.add(favoritesDisposable);
            Log.e(getClass().getSimpleName(), "adding disposable result=" + addResult);
        }
    }

    @Override
    public void clear() {
        sDataCache = null;
        compositeDisposable.clear();
        sIsDisplayed = false;
        interactor.clear();
    }
}
