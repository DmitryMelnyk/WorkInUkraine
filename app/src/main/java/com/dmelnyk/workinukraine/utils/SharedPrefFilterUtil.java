package com.dmelnyk.workinukraine.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.application.DaggerApplicationComponent;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by d264 on 10/10/17.
 */

@Singleton
public class SharedPrefFilterUtil {

    private static final String FILTER_PREF = "FILTER_PREF";
    private static final String FILTER_IS_ENABLE = "FILTER_IS_ENABLE";

    private final WeakReference<Context> context;

    @Inject
    public SharedPrefFilterUtil(Context context) {
        this.context = new WeakReference<Context>(context.getApplicationContext());
    }

    /**
     * @param words - The set of words to be filter
     * @param request - The request and the key for saving words
     */
    public void saveFilterWords(String request, Set<String> words) {
        Timber.i("saveFilterWords, request=%s", request);
        SharedPreferences preferences = context.get().getSharedPreferences(FILTER_PREF, 0);
        preferences.edit().putStringSet(request, words)
                .commit();
    }

    /**
     * @param request - The request for retrieving set of filter's words
     * @return The Set of Strings - list words for filtering vacancies
     */
    @NonNull
    public Set<String> getFilterWords(String request) {
        Timber.i("getFilterWords, request=%s", request);
        return context.get().getSharedPreferences(FILTER_PREF, 0)
                .getStringSet(request, new HashSet<String>());
    }

    public boolean isFilterEnable(String request) {
        Timber.i("isFilterEnable, request=%s", request);
        return context.get().getSharedPreferences(FILTER_PREF, 0)
                .getBoolean(FILTER_IS_ENABLE + request, false);
    }

    public void setFilterEnable(String request, boolean enable) {
        Timber.i("isFilterEnable, request=%s", request);
        context.get().getSharedPreferences(FILTER_PREF, 0)
                .edit()
                .putBoolean(FILTER_IS_ENABLE + request, enable)
                .commit();
    }
}
