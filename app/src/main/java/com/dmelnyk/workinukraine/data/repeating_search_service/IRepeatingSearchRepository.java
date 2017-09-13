package com.dmelnyk.workinukraine.data.repeating_search_service;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by d264 on 9/5/17.
 */

public interface IRepeatingSearchRepository {

    /**
     * @return The list of new vacancies
     */
    Single<List<VacancyModel>> getNewVacancies();

    /**
     * @return The count of request
     */
    int getRequestCount();

    /**
     * @return The count of previous saved new vacancies
     */
    int getPreviousNewVacanciesCount();

    /**
     * Saves new vacancies count to SharedPreference
     * @param newVacancies - count of new vacancies
     */
    void saveNewVacanciesCount(int newVacancies);

    void close();
}
