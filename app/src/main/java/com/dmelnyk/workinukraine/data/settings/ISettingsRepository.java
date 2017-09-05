package com.dmelnyk.workinukraine.data.settings;

/**
 * Created by d264 on 6/27/17.
 */

public interface ISettingsRepository {

    String getPeriodStatesText();
    boolean getSoundCheckedStates();
    boolean getVibroCheckedStates();
    boolean getSleepModeCheckedStates();

    void savePeriodPosition(int checkedNumPosition);
    void saveSoundState(boolean checked);
    void saveVibroState(boolean checked);
    void saveSleepModeStateChecked(boolean checked);

    String getSleepModeStatesFrom();

    String getSleepModeStatesTo();

    String getSoundStateText();

    String getVibroStateText();

    int getPeriodPosition();

    int getPeriodInMillis(int periodIndex);

    void saveSleepModeFromTime(String from);

    void saveSleepModeToTime(String to);
}
