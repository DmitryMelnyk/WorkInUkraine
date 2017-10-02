package com.dmelnyk.workinukraine.services.search.repository;

import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;

import io.reactivex.Observable;

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
    void saveVacancies(List<VacancyModel> stringListMap) throws Exception;

    /**
     * Returns list of RequestModel items
     * Request format in single string: "request / city"
     * @return The Observable that emits list of search requests
     */
    Observable<List<RequestModel>> getRequests();
}
