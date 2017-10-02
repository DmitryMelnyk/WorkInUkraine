package com.dmelnyk.workinukraine.ui.settings.repository;

/**
 * Created by d264 on 6/27/17.
 */

public interface ISettingsRepository {

    String getPeriodStatesText();
    boolean getSoundCheckedStates();
    boolean getVibroCheckedStates();
    boolean getSleepModeCheckedState();

    void savePeriodPosition(int checkedNumPosition);
    void saveSoundState(boolean checked);
    void saveVibroState(boolean checked);
    void saveSleepModeStateChecked(boolean checked);

    String getSleepModeStatesFrom();

    String getSleepModeStatesTo();

    String getSoundStateText();

    String getVibroStateText();

    int getPeriodPosition();

    int getPeriodInMillis();

    int getPeriodInMillis(int periodIndex);

    void saveSleepModeFromTime(String from);

    void saveSleepModeToTime(String to);
}
