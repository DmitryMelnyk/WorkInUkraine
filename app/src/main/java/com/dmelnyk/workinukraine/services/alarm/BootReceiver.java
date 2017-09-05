package com.dmelnyk.workinukraine.services.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

/**
 * Created by d264 on 6/20/17.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Boot Completed!");
        AlarmClockUtil alarmclock = new AlarmClockUtil(context);
        alarmclock.startAlarmClock();
    }
}
