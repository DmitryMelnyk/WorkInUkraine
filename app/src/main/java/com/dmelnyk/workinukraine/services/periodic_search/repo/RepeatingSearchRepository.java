package com.dmelnyk.workinukraine.services.periodic_search.repo;

import android.database.Cursor;

import com.dmelnyk.workinukraine.ui.settings.repository.ISettingsRepository;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.ui.settings.repository.SettingsRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by d264 on 9/5/17.
 */

public class RepeatingSearchRepository implements IRepeatingSearchRepository {

    private static final String TABLE_REQUEST = DbContract.SearchRequest.TABLE_REQUEST;
    private static final String TABLE_VACANCIES = DbContract.SearchSites.TABLE_ALL_SITES;

    private final BriteDatabase db;
    private final ISettingsRepository settingsRepository;
    private final SharedPrefUtil sharedPrefUtil;

    public RepeatingSearchRepository(BriteDatabase db, SettingsRepository settingsRepo, SharedPrefUtil sharedPrefUtil) {
        this.db = db;
        this.settingsRepository = settingsRepo;
        this.sharedPrefUtil = sharedPrefUtil;
    }

    @Override
    public Single<List<VacancyModel>> getNewVacancies() {

        Observable<List<VacancyModel>> result =
                db.createQuery(TABLE_VACANCIES, "SELECT * FROM " + TABLE_VACANCIES
                + " WHERE " + DbContract.SearchSites.Columns.TIME_STATUS
                + " =1").mapToList(VacancyModel.MAPPER);

        return result.firstOrError();
    }

    @Override
    public int getRequestCount() {
        Cursor cursor = db.query("SELECT * FROM " + TABLE_REQUEST);
        int requestCount = cursor.getCount();
        cursor.close();

        return requestCount;
    }

    @Override
    public int getPreviousNewVacanciesCount() {
        return sharedPrefUtil.getPreviousNewVacanciesCount();
    }

    @Override
    public void saveNewVacanciesCount(int newVacancies) {
        sharedPrefUtil.saveNewVacanciesCount(newVacancies);
    }

    @Override
    public void close() {
        db.close();
    }


    @Override
    public boolean isVibroEnable() {
        return settingsRepository.getVibroCheckedStates();
    }

    @Override
    public boolean isSoundEnable() {
        return settingsRepository.getSoundCheckedStates();
    }

    @Override
    public Calendar getCurrentTime() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c;
    }

    @Override
    public Calendar getSleepFromTime() {
        String time = settingsRepository.getSleepModeStatesFrom();
        return getCalendarTime(time);
    }

    @Override
    public Calendar getWakeTime() {
        String time = settingsRepository.getSleepModeStatesTo();
        return getCalendarTime(time);
    }

    @Override
    public long getUpdateInterval() {
        return settingsRepository.getPeriodInMillis();
    }

    @Override
    public boolean isSleepModeEnabled() {
        return settingsRepository.getSleepModeCheckedState();
    }

    @Override
    public boolean isPeriodicSearchEnable() {
        return settingsRepository.getPeriodicSearchCheckedState();
    }

    private Calendar getCalendarTime(String time) {
        String hour = time.split(":")[0];
        String minute = time.split(":")[1];

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        c.set(Calendar.MINUTE, Integer.valueOf(minute));
        c.set(Calendar.SECOND, 0);

        return c;
    }
}
