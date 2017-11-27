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
import io.reactivex.schedulers.Schedulers;

import static com.dmelnyk.workinukraine.ui.vacancy_list.business.IVacancyListInteractor.DATA_FAVORITE;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyListPresenter implements Contract.IVacancyPresenter {

    private static final int DEFAULT_BUTTON_TAB_TYPE = -1;
    private static final int[] DEFAULT_TAV_VACANCY_COUNT = null;
    private final IVacancyListInteractor interactor;
    private Contract.IVacancyView view;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable favoritesDisposable;

    private static Map<String, List<VacancyModel>> sDataCache;
    private static boolean sIsDisplayed;
    private static String mRequest;
    private static int sButtonTabType = DEFAULT_BUTTON_TAB_TYPE;
    private static int[] sTabVacancyCount = DEFAULT_TAV_VACANCY_COUNT;

    public VacancyListPresenter(IVacancyListInteractor interactor, String request) {
        this.interactor = interactor;
        this.mRequest = request;
        sTabVacancyCount = DEFAULT_TAV_VACANCY_COUNT;
        sButtonTabType = DEFAULT_BUTTON_TAB_TYPE;
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
            displayData(sDataCache, false);
        } else {
            getAllVacancies(request);
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
    }
    @Override
    public Pair<Boolean, Set<String>> getFilterItems() {
        return interactor.getFilterItems();
    }

    @Override
    public void filterUpdated(Pair<Boolean, Set<String>> filters) {
        sIsDisplayed = false;
        compositeDisposable.add(
                interactor.updateFilter(filters)
                .subscribe(() -> {
                    interactor.getFilteredVacancies()
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(vacanciesMap -> {
                                sDataCache = new HashMap<>(vacanciesMap);
                                if (view != null) {
                                    sIsDisplayed = true;
                                    displayData(sDataCache, true);
                                }
                            });
                }));
    }

    private void displayData(Map<String, List<VacancyModel>> vacanciesMap, boolean isFiltered) {
        // Counting vacancies count in all tabs
        int siteTabsCount = vacanciesMap.get(IVacancyListInteractor.DATA_ALL).size();
        int newVacanciesCount = vacanciesMap.get(IVacancyListInteractor.DATA_NEW).size();
        int recentVacanciesCount = vacanciesMap.get(IVacancyListInteractor.DATA_RECENT).size();
        int favoriteVacanciesCount = vacanciesMap.get(DATA_FAVORITE).size();

        // Exit from activity if no vacancies found
        if (siteTabsCount == 0) {
            Log.d(getClass().getSimpleName(), "displayData(). No vacancy has found. Exit from activity!");
            view.exitActivity();
            clear();
            return;
        }

        if (true/*sTabVacancyCount == DEFAULT_TAV_VACANCY_COUNT*/) {
            sTabVacancyCount = newVacanciesCount > 0 && recentVacanciesCount > 0
                    ? new int[4] : new int[3];
        } else {
            Log.e(getClass().getSimpleName(), "sTabVacancyCount is already defined =" + sTabVacancyCount);
        }

        sTabVacancyCount[0] = siteTabsCount; // first item is always the same
        // sButtonTabType: -1 - not defined yet, 1 - only new vacancies, 2 - new and recent, 3 - only recent
        if (true/*sButtonTabType == DEFAULT_BUTTON_TAB_TYPE*/) {
            if (newVacanciesCount > 0 && recentVacanciesCount == 0) {
                sButtonTabType = 1;
            }
            if (newVacanciesCount > 0 && recentVacanciesCount > 0) {
                sButtonTabType = 2;
            }
            if (newVacanciesCount == 0) {
                sButtonTabType = 3;
            }
        } else {
            Log.e(getClass().getSimpleName(), "sButtonTabType is already defined =" + sButtonTabType);
        }

        String[] tabTitles = null;
        switch (sButtonTabType) {
            case 1:
                tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_NEW);
                sTabVacancyCount[1] = newVacanciesCount;
                sTabVacancyCount[2] = favoriteVacanciesCount;
                break;
            case 2:
                tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_NEW_AND_RECENT);
                sTabVacancyCount[1] = newVacanciesCount;
                sTabVacancyCount[2] = recentVacanciesCount;
                sTabVacancyCount[3] = favoriteVacanciesCount;
                break;
            case 3:
                tabTitles = interactor.getTitles(IVacancyListInteractor.TITLE_RECENT);
                sTabVacancyCount[1] = recentVacanciesCount;
                sTabVacancyCount[2] = favoriteVacanciesCount;
                break;
        }

        view.displayTabFragment(tabTitles, sTabVacancyCount, sButtonTabType, vacanciesMap, isFiltered);
    }

    private void getAllVacancies(String request) {
        compositeDisposable.add(
                interactor.getAllVacancies(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(vacanciesMap -> {
                    sDataCache = new HashMap<>(vacanciesMap);
                    if (view != null) {
                        sIsDisplayed = true;
                        displayData(sDataCache, false);
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
        Log.e(getClass().getSimpleName(), "updateFavorites is called. Request = " + mRequest);

        if (favoritesDisposable != null) {
            favoritesDisposable.dispose();
        }

        favoritesDisposable = interactor.getFavoriteVacancies(mRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favorites -> {
                    Log.e(getClass().getSimpleName(), "updateFavorites is called. Favorites=" + favorites);
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

    @Override
    public void clear() {
        interactor.clear();
        compositeDisposable.clear();
        sDataCache = null;
        sTabVacancyCount = null;
        sIsDisplayed = false;
        sButtonTabType = DEFAULT_BUTTON_TAB_TYPE;
    }
}
