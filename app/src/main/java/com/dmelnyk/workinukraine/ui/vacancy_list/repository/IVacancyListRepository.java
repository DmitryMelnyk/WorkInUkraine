package com.dmelnyk.workinukraine.ui.vacancy_list.data;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyListRepository {

    /**
     * Converts 'new' vacancies to 'recent' and 'recent' to old
     * This method should be called after displaying new vacancies
     * @param request The request - vacancies with request that should be updated.
     */
    void updateTimeStatusVacancies(String request);

    Observable<List<VacancyModel>> getFavoriteVacancies(String request);

    /**
     * Removes vacancy from TYPE_FAVORITE table
     * @param vacancy The item that should be removed
     * @return
     */
    Completable removeFromFavorites(VacancyModel vacancy);

    Completable addToFavorite(VacancyModel vacancy);

    Observable<Map<String, List<VacancyModel>>> getAllVacancies(String request);

    /**
     * @return The array of Strings titles from resources with New tab
     */
    String[] getNewTitles();

    /**
     * @return The array of Strings titles from resources with Recent tab
     */
    String[] getRecentTitles();

    /**
     * @return The array of Strings titles from resources with New and Recent tab
     */
    String[] getNewAndRecent();

    void close();
}
