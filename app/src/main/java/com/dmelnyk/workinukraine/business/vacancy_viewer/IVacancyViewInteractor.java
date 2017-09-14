package com.dmelnyk.workinukraine.business.vacancy_viewer;

import com.dmelnyk.workinukraine.models.VacancyModel;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by d264 on 9/14/17.
 */

public interface IVacancyViewInteractor {

    /**
     * Changes vacancy 'favorite' property (true / false)
     * @param vacancy
     */
    Single<Boolean> updateFavorite(VacancyModel vacancy);
}
