package com.dmelnyk.workinukraine.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by d264 on 10/2/17.
 */

@Singleton
public class SharedPrefUtil {
    private static final String PREF_FILE = "saved_vacancies_count";
    private static final String KEY_PREVIOUS_NEW_VACANCIES_COUNT = "KEY_PREVIOUS_NEW_VACANCIES_COUNT";
    private static final String KEY_IS_SEARCHING_RUNNING = "KEY_IS_SEARCHING_RUNNING";

    private final Context context;

    @Inject
    public SharedPrefUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public int getPreviousNewVacanciesCount() {
        Timber.i("getPreviousNewVacanciesCount");
        return context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
                .getInt(KEY_PREVIOUS_NEW_VACANCIES_COUNT, 0);
    }

    public void saveNewVacanciesCount(int newVacancies) {
        Timber.i("saveNewVacanciesCount=" + newVacancies);
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
                .putInt(KEY_PREVIOUS_NEW_VACANCIES_COUNT, newVacancies)
                .commit();
    }



    public void clearData() {
        Timber.i("clearData");
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
                .clear()
                .commit();
    }

    public boolean shouldBeUpdated(String request) {
        Timber.i("shouldBeUpdated, request=" + request);
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, 0);
        return preferences.getBoolean(request, false);
    }

    public void saveUpdatingTask(String request, boolean shouldBeUpdated) {
        Timber.i("saveUpdatingTask, request=%s, shouldBeUpdated=%s", request, "" + shouldBeUpdated);
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, 0);
        preferences.edit().putBoolean(request, shouldBeUpdated).commit();
    }

    public static boolean isRepeatingSearchRunning(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, 0);
        boolean isRunning = preferences.getBoolean(KEY_IS_SEARCHING_RUNNING, false);
        Timber.i("isRepeatingSearchRunning=" + isRunning);
        return isRunning;
    }

    public static void setRepeatingSearchStarted(Context context) {
        Timber.i("setRepeatingSearchStarted");
        SharedPreferences preferences = context.getSharedPreferences(PREF_FILE, 0);
        preferences.edit().putBoolean(KEY_IS_SEARCHING_RUNNING, true).commit();
    }
}
