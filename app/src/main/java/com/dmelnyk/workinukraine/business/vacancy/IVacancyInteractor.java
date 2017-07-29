package com.dmelnyk.workinukraine.business.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy.CardViewAdapter;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyInteractor {
    /**
     * Returns a list of vacancies
     * @param request Vacancy request in format "request / city"
     * @param table Table where to get data
     * @return Observable List of vacancies
     */
    Observable<List<VacancyModel>> getVacancies(String request, String table);

    /**
     * Removes or saves to favorite vacancy
     * @param vacancy Vacancy that should be treated
     * @param operation Type of operation that should be done (save/remove to FAVORITE table)
     * @return Completable? the result of operation.
     */
    Completable onPopupMenuClicked(VacancyModel vacancy,
                                   @CardViewAdapter.VacancyPopupMenuType int operation);

}
