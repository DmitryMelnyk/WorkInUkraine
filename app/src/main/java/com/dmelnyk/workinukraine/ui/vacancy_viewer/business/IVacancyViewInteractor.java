package com.dmelnyk.workinukraine.business.vacancy_viewer;

import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;

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

    /**
     * @param request - The request of the vacancies
     * @param type - The type of vacancies (new, recent, favorite and 5 types of sites
     * @return
     */
    Single<List<VacancyModel>> getVacancies(String request, String type, @Nullable String site);
}
