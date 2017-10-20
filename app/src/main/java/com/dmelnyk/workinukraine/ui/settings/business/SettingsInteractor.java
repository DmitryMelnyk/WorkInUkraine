package com.dmelnyk.workinukraine.ui.settings.business;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.ui.settings.repository.ISettingsRepository;

/**
 * Created by d264 on 6/27/17.
 */

public class SettingsInteractor implements ISettingsInteractor {

    private ISettingsRepository repository;

    public SettingsInteractor(ISettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean[] getCheckedStates() {
        boolean[] states = new boolean[3];
        states[0] = repository.getSoundCheckedStates();
        states[1] = repository.getVibroCheckedStates();
        states[2] = repository.getSleepModeCheckedState();
        return states;
    }

    @Override
    public String[] getTextStates() {
        String states[] = new String[5];
        states[0] = repository.getPeriodStatesText();
        states[1] = repository.getSoundStateText();
        states[2] = repository.getVibroStateText();
        states[3] = "| " + repository.getSleepModeStatesFrom();
        states[4] = repository.getSleepModeStatesTo();

        return states;
    }

    @Override
    public String getSleepModeFromTime() {
        return repository.getSleepModeStatesFrom();
    }

    @Override
    public String getSleepModeToTime() {
        return repository.getSleepModeStatesTo();
    }

    @Override
    public int getPeriodPosition() {
        return repository.getPeriodPosition();
    }

    // ---------------------------------------

    @Override
    public void savePeriodPosition(int checkedNumPosition) {
        int previousPosition = repository.getPeriodPosition();
        if (previousPosition != checkedNumPosition) {
            repository.savePeriodPosition(checkedNumPosition);
            updateRepeatingJob(checkedNumPosition);
        }
    }

    @Override
    public void saveSoundCheckedState(boolean checked) {
        repository.saveSoundState(checked);
    }

    @Override
    public void saveVibroCheckedState(boolean checked) {
        repository.saveVibroState(checked);
    }

    @Override
    public void saveSleepModeCheckedState(boolean checked) {
        repository.saveSleepModeStateChecked(checked);
    }

    @Override
    public void saveSleepModeFrom(String from) {
        repository.saveSleepModeFromTime(from);
    }

    @Override
    public void saveSleepModeTo(String to) {
        repository.saveSleepModeToTime(to);
    }

    private void updateRepeatingJob(int periodIndex) {
        long intervalInMillis = repository.getPeriodInMillis(periodIndex);
        RepeatingSearchJob.scheduleRepeatingSearch(intervalInMillis);
    }

    // TODO: turn off notification
    @Override
    public void onSwitcherClicked(int position, boolean checked) {
        switch (position) {
            case 0:
                repository.saveSoundState(checked);
                break;
            case 1:
                repository.saveVibroState(checked);
                break;
            case 2:
                repository.saveSleepModeStateChecked(checked);
        }
    }
}
