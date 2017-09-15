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

    String VACANCIES_ALL = "all";
    String VACANCIES_FAVORITE = "TYPE_FAVORITE";
    String VACANCIES_RECENT = "TYPE_RECENT";
    String VACANCIES_NEW = "FRESH";
    String VACANCIES_SITE_HH = "HEADHUNTERSUA";
    String VACANCIES_SITE_JU = "JOBSUA";
    String VACANCIES_SITE_RU = "RABOTAUA";
    String VACANCIES_SITE_WN = "WORKNEWINFO";
    String VACANCIES_SITE_WU = "WORKUA";
    String DATA_ALL = "all_vacancies";
    String DATA_NEW = "new_vacancies";
    String DATA_RECENT = "recent_vacancies";
    String DATA_FAVORITE = "favorite_vacancies";

    String TITLE_NEW = "new_titles";
    String TITLE_RECENT = "recent_titles";
    String TITLE_NEW_AND_RECENT = "new_and_recent";

    @StringDef({ TITLE_NEW, TITLE_NEW_AND_RECENT, TITLE_RECENT })
    @Retention(RetentionPolicy.CLASS)
    public @interface TitleResource {}

//    @StringDef({ VACANCIES_ALL, VACANCIES_FAVORITE, VACANCIES_NEW, VACANCIES_RECENT,
//            VACANCIES_SITE_HH, VACANCIES_SITE_JU, VACANCIES_SITE_RU,
//            VACANCIES_SITE_WN, VACANCIES_SITE_WU})
//    @Retention(RetentionPolicy.CLASS)
//    public @interface VacancyResource {}

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
