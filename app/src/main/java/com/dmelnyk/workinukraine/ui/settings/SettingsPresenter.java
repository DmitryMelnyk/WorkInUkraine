package com.dmelnyk.workinukraine.ui.settings;

import com.dmelnyk.workinukraine.business.settings.ISettingsInteractor;
import com.dmelnyk.workinukraine.ui.settings.Contract.ISettingsView;

/**
 * Created by d264 on 6/27/17.
 */

public class SettingsPresenter implements Contract.ISettingsPresenter {

    public static final int TIME_FROM = 0;
    public static final int TIME_TO = 1;
    private int timeMode;

    private ISettingsInteractor interactor;
    private ISettingsView view;

    public SettingsPresenter(ISettingsInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void bindView(ISettingsView view) {
        this.view = view;
        if (view != null) {
            updateViews();
        }

    }

    private void updateViews() {
        boolean[] checkedStates = interactor.getCheckedStates();
        String[] textStates = interactor.getTextStates();

        view.initializeViews(checkedStates, textStates);
    }

    @Override
    public void unbindView() {
        view = null;
    }

    @Override
    public void onSwitcherChecked(int position, boolean checked) {
        // Save state and make some updates
        interactor.onSwitcherClicked(position, checked);
        updateViews();
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public void onPeriodItemClicked() {
        view.showDialogPeriodChooser(interactor.getPeriodPosition());
    }

    @Override
    public void onPeriodChose(int checkedNumPosition) {
        interactor.savePeriodPosition(checkedNumPosition);
        updateViews();
    }

    @Override
    public void onDontDisturbTimeModeChose(int mode) {
        timeMode = mode;
        switch (mode) {
            case TIME_FROM:
                String savedTime = interactor.getSleepModeFromTime();
                view.showTimePicker(savedTime);
                break;
            case TIME_TO:
                savedTime = interactor.getSleepModeToTime();
                view.showTimePicker(savedTime);
        }
    }

    @Override
    public void onTimeSelected(String time) {
        switch (timeMode) {
            case TIME_FROM:
                view.updateFromTextView(time);
                interactor.saveSleepModeFrom(time);
                break;
            case TIME_TO:
                view.updateToTextView(time);
                interactor.saveSleepModeTo(time);
                break;

        }
    }
}
