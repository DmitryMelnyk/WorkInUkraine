package com.dmelnyk.workinukraine.data.search_service;

import com.dmelnyk.workinukraine.models.VacancyContainer;

import java.util.List;

/**
 * Created by d264 on 7/25/17.
 */

public interface ISearchServiceRepository {

    /**
     * Closes database
     */
    void closeDb();

    /**
     * Saves vacancies from all websites
     * @param stringListMap
     */
    void saveVacancies(List<VacancyContainer> stringListMap);
}
