package com.dmelnyk.workinukraine.ui.settings.business;

import android.util.Log;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.ui.settings.repository.ISettingsRepository;
import com.evernote.android.job.JobManager;

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
        boolean[] states = new boolean[4];
        states[0] = repository.getSoundCheckedStates();
        states[1] = repository.getVibroCheckedStates();
        states[2] = repository.getSleepModeCheckedState();
        states[3] = repository.getPeriodicSearchCheckedState();
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
        repository.saveSleepModeStateEnable(checked);
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
                repository.saveSleepModeStateEnable(checked);
                break;
            case 3:
                repository.savePeriodicSearchEnable(checked);
                if (checked) {
                    runRepeatingJob();
                }
                break;
            default:
                throw new Error("Switcher interaction case not realized errot!");
        }
    }

    private void runRepeatingJob() {
        long intervalInMillis = repository.getPeriodInMillis();
        RepeatingSearchJob.scheduleRepeatingSearch(intervalInMillis);
    }
}
