package com.dmelnyk.workinukraine.services.periodic_search.repo;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.Calendar;
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

    /**
     * @return The info about vibration settings for notification.
     */
    boolean isVibroEnable();

    /**
     * @return The info about sound settings for notification.
     */
    boolean isSoundEnable();

    /**
     * @return The calendar with current time
     */
    Calendar getCurrentTime();

    /**
     * @return The calendar with time when app should STOP receiving new vacancies
     */
    Calendar getSleepFromTime();

    /**
     * @return The calendar with time when app should START receiving new vacancies
     */
    Calendar getWakeTime();

    /**
     * @return The time for repeating search in milliseconds
     */
    long getUpdateInterval();

    /**
     * @return The status of enabling repeated search
     */
    boolean isSleepModeEnabled();
}
