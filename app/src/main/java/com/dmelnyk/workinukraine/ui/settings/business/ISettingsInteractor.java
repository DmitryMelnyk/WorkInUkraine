package com.dmelnyk.workinukraine.business.settings;

/**
 * Created by d264 on 6/27/17.
 */


public interface ISettingsInteractor {
    boolean[] getCheckedStates();

    String[] getTextStates();

    String getSleepModeToTime();

    int getPeriodPosition();

    void savePeriodPosition(int checkedNumPosition);
    void saveSoundCheckedState(boolean checked);
    void saveVibroCheckedState(boolean checked);
    void saveSleepModeCheckedState(boolean checked);

//    void updateRepeatingAlarm(int periodIndex);

    void saveSleepModeFrom(String from);

    void saveSleepModeTo(String to);

    void onSwitcherClicked(int position, boolean checked);

    String getSleepModeFromTime();
}
