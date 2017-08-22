package com.dmelnyk.workinukraine.model.search_service;

import com.dmelnyk.workinukraine.data.VacancyContainer;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/25/17.
 */

public interface ISearchServiceRepository {

    /**
     * Closes database
     */
    void closeDb();

    /**
     * Updates REQUEST table
     * @param request
     * @param integer
     * @param updateTime
     */
    void updateRequestTable(String request, Integer integer, long updateTime);

    /**
     * Saves vacancies from all websites
     * @param stringListMap
     */
    void saveVacancies(List<VacancyContainer> stringListMap);
}
