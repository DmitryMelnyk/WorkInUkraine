package com.dmelnyk.workinukraine.data.settings;

import android.content.Context;

import com.dmelnyk.workinukraine.R;

import javax.inject.Inject;

/**
 * Created by d264 on 6/27/17.
 */

public class SettingsRepository implements ISettingsRepository {

    private static final String SETTINGS = "settings";
    private static final int PERIOD_DEFAULT = 3; // 0 - 30 min, 1 - hour, 2 - 3 hours, 3 - 6 hours,..
    private static final String PERIOD = "period";
    private static final boolean CHECKED_DEFAULT = true;
    private static final String SOUND = "sound";
    private static final String VIBRO = "vibro";
    private static final String SLEEP_MODE = "sleep mode";
    private static final String SLEEP_MODE_FROM = "sleep mode from";
    private static final String SLEEP_MODE_TO = "sleep mode to";
    private static final String SLEEP_MODE_FROM_DEFAULT = "23:00";
    private static final String SLEEP_MODE_TO_DEFAULT = "7:00";


    private Context mContext;

    @Inject
    public SettingsRepository(Context mContext) {
        this.mContext = mContext;
    }

    // ------------G E T T E R S----------------

    @Override
    public int getPeriodPosition() {
        return mContext.getSharedPreferences(SETTINGS, 0)
                .getInt(PERIOD, PERIOD_DEFAULT);
    }

    @Override
    public int getPeriodInMillis() {
        int position = getPeriodPosition();
        return getPeriodInMillis(position);
    }

    @Override
    public int getPeriodInMillis(int periodIndex) {
        int[] periodsInMillisArray = mContext.getResources()
                .getIntArray(R.array.settings_period_number_milliseconds);
        return periodsInMillisArray[periodIndex];
    }

    @Override
    public String getPeriodStatesText() {
        int periodPosition = getPeriodPosition();

        String[] array = mContext.getResources().getStringArray(R.array.settings_period_summary);
        return array[periodPosition];
    }

    @Override
    public boolean getSoundCheckedStates() {
        return mContext.getSharedPreferences(SETTINGS, 0)
                .getBoolean(SOUND, CHECKED_DEFAULT);
    }

    @Override
    public boolean getVibroCheckedStates() {
        return mContext.getSharedPreferences(SETTINGS, 0)
                .getBoolean(VIBRO, CHECKED_DEFAULT);
    }

    @Override
    public boolean getSleepModeCheckedState() {
        return mContext.getSharedPreferences(SETTINGS, 0)
                .getBoolean(SLEEP_MODE, CHECKED_DEFAULT);
    }

    @Override
    public String getSleepModeStatesFrom() {
        String from = mContext.getSharedPreferences(SETTINGS, 0)
                .getString(SLEEP_MODE_FROM, SLEEP_MODE_FROM_DEFAULT);

        return from;
    }

    @Override
    public String getSleepModeStatesTo() {
        String to = mContext.getSharedPreferences(SETTINGS, 0)
                .getString(SLEEP_MODE_TO, SLEEP_MODE_TO_DEFAULT);

        return to;
    }

    @Override
    public String getSoundStateText() {
        return getSoundCheckedStates()
                ? mContext.getString(R.string.settings_cheched_on)
                : mContext.getString(R.string.settings_cheched_of);
    }

    @Override
    public String getVibroStateText() {
        return getVibroCheckedStates()
                ? mContext.getString(R.string.settings_cheched_on)
                : mContext.getString(R.string.settings_cheched_of);
    }

    // ------------S E T T E R S----------------

    @Override
    public void savePeriodPosition(int period) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putInt(PERIOD, period)
                .commit();
    }

    @Override
    public void saveSoundState(boolean checked) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putBoolean(SOUND, checked)
                .commit();
    }

    @Override
    public void saveVibroState(boolean checked) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putBoolean(VIBRO, checked)
                .commit();
    }

    @Override
    public void saveSleepModeStateChecked(boolean checked) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putBoolean(SLEEP_MODE, checked)
                .commit();
    }

    @Override
    public void saveSleepModeFromTime(String from) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putString(SLEEP_MODE_FROM, from)
                .commit();
    }

    @Override
    public void saveSleepModeToTime(String to) {
        mContext.getSharedPreferences(SETTINGS, 0).edit()
                .putString(SLEEP_MODE_TO, to)
                .commit();
    }

}
