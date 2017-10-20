package com.dmelnyk.workinukraine.ui.settings;

/**
 * Created by d264 on 6/27/17.
 */

public class Contract {

    public interface ISettingsView {
        void initializeViews(boolean[] checkedStates, String[] textStates);

        void showTimePicker(String time);

        void showDialogPeriodChooser(int repeatingPosition);

        void updateFromTextView(String time);

        void updateToTextView(String time);
    }

    public interface ISettingsPresenter {
        void bindView(ISettingsView view);
        void unbindView();

        void onSwitcherChecked(int position, boolean checked);

        void onPeriodChose(int checkedItemPosition);

        void onPeriodItemClicked();

        void onDontDisturbTimeModeChose(int mode);

        void onTimeSelected(String time);
    }
}
