package com.dmelnyk.workinukraine.services.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.dmelnyk.workinukraine.data.repeating_search_service.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.alarm.di.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.services.alarm.di.RepeatingSearchModule;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Created by d264 on 6/17/17.
 */

public class AlarmClockUtil {

    @Inject IRepeatingSearchRepository repository;

    private static final long INTERVAL = 1000 * 60 * 5; // 5 minutes AlarmManager.INTERVAL_HOUR;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AlarmClockUtil(Context context) {

        DaggerRepeatingSearchComponent.builder()
                .dbModule(new DbModule(context.getApplicationContext()))
                .build()
                .inject(this);

        Log.e("999", "requestCount=" + repository.getRequestCount());

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

    /**
     * Cancels pending alarm intent
     */
    public void stopAlarmClock() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }
}
