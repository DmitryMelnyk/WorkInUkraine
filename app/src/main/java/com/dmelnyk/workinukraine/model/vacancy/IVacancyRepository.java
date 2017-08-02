package com.dmelnyk.workinukraine.model.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyRepository {


    Observable<List<VacancyModel>> getVacancies(String request, String type);

    /**
     * Removes vacancy from FAVORITE table
     * @param vacancy The item that should be removed
     * @return
     */
    Completable removeFromFavorites(VacancyModel vacancy);

    Completable saveToFavorite(VacancyModel vacancy);

    Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request);
}
