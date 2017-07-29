package com.dmelnyk.workinukraine.ui.dialogs.time_picker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 * Created by d264 on 7/6/17.
 */

public class DialogTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private static final String ARG_TIME = "time";
    private OnDialogTimePickerInteractionListener mCallback;

    public static DialogTimePicker getNewInstance(String time) {
        Bundle args = new Bundle();
        args.putString(ARG_TIME, time);
        DialogTimePicker dialogFragment = new DialogTimePicker();
        dialogFragment.setArguments(args);
        return dialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        String time = getArguments().getString(ARG_TIME);
        int hour = Integer.parseInt(time.split(":")[0]);
        int minute = Integer.parseInt(time.split(":")[1]);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getContext(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String minuteFormatted = minute == 0 ? "00" : "" + minute;
        String time = "" + hourOfDay + ":" + minuteFormatted;
        mCallback.onTimeSet(time);
    }

    public interface OnDialogTimePickerInteractionListener {
        void onTimeSet(String time);
    }

    public void setInteractionListener(OnDialogTimePickerInteractionListener listener) {
        mCallback = listener;
    }
}
