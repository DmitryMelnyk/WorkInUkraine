package com.dmelnyk.workinukraine.services.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.SystemClock;
import android.util.Log;

import com.dmelnyk.workinukraine.services.alarm.repo.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.alarm.di.DaggerRepeatingSearchComponent;

import java.util.Date;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Created by d264 on 6/17/17.
 */

public class AlarmClockUtil {

    @Inject IRepeatingSearchRepository repository;

    private long INTERVAL = AlarmManager.INTERVAL_HOUR * 3;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AlarmClockUtil(Context context) {

        DaggerRepeatingSearchComponent.builder()
                .dbModule(new DbModule(context.getApplicationContext()))
                .build()
                .inject(this);

        INTERVAL = repository.getUpdateInterval();

        alarmManager = (AlarmManager) context.getApplicationContext()
                .getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
    }

    /**
     * Creates repeating alarm with inexact recurrence interval.
     */
    public void startAlarmClock() {
        if (repository.getRequestCount() == 0) {
            // don't start repeating alarm if there is no request
            Log.e("999", "repeating alarm doesn't start because of empty request list");
            return;
        }


        Timber.d("AlarmClock starts!");
        Log.e("999", "alarm started!");
        stopAlarmClock();
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + INTERVAL, INTERVAL, alarmIntent);
    }

    public void startAlarmClockAtTime(long time) {
        if (repository.getRequestCount() == 0) {
            // don't start repeating alarm if there is no request
           Timber.d("repeating alarm doesn't start because of empty request list");
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy, hh:mm");
        String nextAlarm = format.format(new Date(time));
        Timber.d("AlarmClock will be running at =" + nextAlarm);
        stopAlarmClock();
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                time, INTERVAL, alarmIntent);
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
