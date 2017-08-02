package com.dmelnyk.workinukraine.model.search_service;

import com.dmelnyk.workinukraine.data.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.List;

/**
 * Created by d264 on 7/25/17.
 */

public interface ISearchServiceRepository {

    /**
     * Saves vacancies to proper table in database
     * @param table The table's name
     * @param list The list of vacancies
     */
    void saveVacancies(String table, List<VacancyModel> list);

    /**
     * Clears fresh vacancies table
     */
    void clearNewTable();

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
     * Says to Repository that downloading tasks are finished to perform
     * saving data to RECENT tables;
     */
    void saveRecentVacancies();
}
