package com.dmelnyk.workinukraine.services.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import timber.log.Timber;


/**
 * Created by d264 on 6/17/17.
 */

public class AlarmClockUtil {

    private static final long INTERVAL = AlarmManager.INTERVAL_HOUR;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AlarmClockUtil(Context mContext) {
        alarmManager = (AlarmManager) mContext.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, intent, 0);
    }

    /**
     * Creates repeating alarm with inexact recurrence interval.
     */
    public void startAlarmClock() {
        Timber.d("AlarmClock starts!");
        Log.e("999", "alarm started!");
        stopAlarmClock();
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, alarmIntent);
    }

    /**
     * Cancels pending alarm intent
     */
    public void stopAlarmClock() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }
}
