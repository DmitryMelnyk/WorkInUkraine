package com.dmelnyk.workinukraine.ui.vacancy_viewer.repository;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;
import java.util.Set;

import io.reactivex.Single;

/**
 * Created by d264 on 9/14/17.
 */

public interface IVacancyViewerRepository {

    /**
     * Changes property favorite (true / false). Also changes date in Favorite table
     * @param vacancy
     * @return The status of operations
     */
    Single<Boolean> updateFavorite(VacancyModel vacancy);

    Single<List<VacancyModel>> getSiteVacancies(String request, String site);

    void close();

    Single<List<VacancyModel>> getFavoriteVacancies(String request);

    Single<List<VacancyModel>> getNewVacancies(String request);

    Single<List<VacancyModel>> getRecentVacancies(String request);

    boolean isFilterEnable(String request);

    Set<String> getFilterWords(String request);
}
