package com.dmelnyk.workinukraine.data.vacancy;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyRepository {


    Observable<List<VacancyModel>> getFavoriteVacancies(String request, String type);

    /**
     * Removes vacancy from TYPE_FAVORITE table
     * @param vacancy The item that should be removed
     * @return
     */
    Completable removeFromFavorites(VacancyModel vacancy);

    Completable saveToFavorite(VacancyModel vacancy);

    Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request);

    /**
     * @return The array of Strings titles from resources with New tab
     */
    String[] getNewTitles();

    /**
     * @return The array of Strings titles from resources with Recent tab
     */
    String[] getRecentTitles();

    void close();
}
