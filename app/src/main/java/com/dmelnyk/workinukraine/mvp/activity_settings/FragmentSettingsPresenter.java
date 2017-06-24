package com.dmelnyk.workinukraine.mvp.activity_settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.di.component.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.helpers.RepeatingSearch;

import javax.inject.Inject;

/**
 * Created by dmitry on 07.04.17.
 */
public class FragmentSettingsPresenter implements Contract.Presenter {

    private static final String TAG = "GT.FragmentSetPres";
    Context context;
    FragmentSettings view;

    String KEY_SOUND_SWITCHER;
    String KEY_SOUND_PERIOD;
    String KEY_LIST_PREFERENCES;
    String KEY_SETTINGS_THEME;

    @Inject
    RepeatingSearch repeatingSearch;

    public FragmentSettingsPresenter(Context context) {
        this.context = context;

        initializeDependency(context);

        KEY_SOUND_SWITCHER = context.getResources().getString(R.string.key_sound_switcher);
        KEY_LIST_PREFERENCES = context.getResources().getString(R.string.key_list_preferences);
        KEY_SOUND_PERIOD = context.getResources().getString(R.string.key_sound_period);
        KEY_SETTINGS_THEME = context.getResources().getString(R.string.key_settings_theme);
    }

    private void initializeDependency(Context context) {
        DaggerRepeatingSearchComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTakeView(FragmentSettings fragment) {
        view = fragment;
        if (view != null) {
            initializeDefaultSummaries();
        }
    }

    private void initializeDefaultSummaries() {
        // Period preference
        Preference periodPreference = view.findPreference(KEY_SOUND_PERIOD);
        String period = view.getPreferenceManager().
                getSharedPreferences().getString(KEY_SOUND_PERIOD, "3 часа");

        String summary = getSummary(period);
        periodPreference.setSummary(summary);
    }

    @Override
    public void onChangeSettings(SharedPreferences sharedPreferences, String key) {
        Preference preference = view.findPreference(key);
        if (key.equals(KEY_SOUND_SWITCHER)) {
            boolean value = sharedPreferences.getBoolean(key, true);
            Log.d(TAG, "change value to " + value);

            // run/cancel periodic search
            repeatingSearch.createRepeating();
            // TODO
        } else if (key.equals(KEY_SOUND_PERIOD)) {
            updateIntervalSummary(sharedPreferences, key, preference);
            repeatingSearch.createRepeating();

        } else if (key.equals(KEY_LIST_PREFERENCES)) {
        } /*else if (key.equals(KEY_SETTINGS_THEME)) {
            Toast.makeText(context, "You want to change theme", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void updateIntervalSummary(SharedPreferences sharedPreferences, String key, Preference preference) {
        String period = sharedPreferences.getString(key, "null");
        preference.setSummary(getSummary(period));
    }

    @NonNull
    private String getSummary(String period) {
        final String[] arrayPeriod = context.getResources().getStringArray(R.array.settings_message_period);
        final String[] arrayPeriodSummary = context.getResources().getStringArray(R.array.settings_period_summary);

        for (int i = 0; i < arrayPeriod.length; i++) {
            if (period.equals(arrayPeriod[i])) {
                return arrayPeriodSummary[i];
            }
        }
        return "";
    }
}
