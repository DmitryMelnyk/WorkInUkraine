package com.dmelnyk.workinukraine.business.vacancy_list;

import android.support.annotation.StringDef;

import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyListInteractor {

    String DATA_ALL = "all_vacancies";
    String DATA_NEW = "new_vacancies";
    String DATA_RECENT = "recent_vacancies";
    String DATA_FAVORITE = "favorite_vacancies";

    String TITLE_NEW = "new_titles";
    String TITLE_RECENT = "recent_titles";
    String TITLE_NEW_AND_RECENT = "new_and_recent";

    void onVacanciesViewed(String request);

    @StringDef({ TITLE_NEW, TITLE_NEW_AND_RECENT, TITLE_RECENT })
    @Retention(RetentionPolicy.CLASS)
    public @interface TitleResource {}

    /**
     * Clearing all resources
     */
    void clear();


    /** Returns HasMap of key-values with 4 types of vacancies:
     * all, new, recent, favorite
     * @param request Vacancy request in format "request / city"
     * @return
     */
    Observable<Map<String, List<VacancyModel>>> getAllVacancies(String request);

    /**
     * Returns a list of vacancies
     * @param request Vacancy request in format "request / city"
     * @return Observable List of vacancies
     */
    Observable<List<VacancyModel>> getFavoriteVacancies(String request);

    /**
     * @param type The type of vacancies: new or recent
     * @return Array of String values
     */
    String[] getTitles(@TitleResource String type);
    /**
     * Removes or saves to favorite vacancy
     * @param vacancy Vacancy that should be treated
     * @param operation Type of operation that should be done (save/remove to TYPE_FAVORITE table)
     * @return Completable? the result of operation.
     */
    Completable onPopupMenuClicked(VacancyModel vacancy,
                                   @VacancyCardViewAdapter.VacancyPopupMenuType int operation);

}
