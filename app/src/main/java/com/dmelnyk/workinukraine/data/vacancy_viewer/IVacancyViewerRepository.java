package com.dmelnyk.workinukraine.data.vacancy_viewer;

import com.dmelnyk.workinukraine.models.VacancyModel;

import io.reactivex.Completable;
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
}
