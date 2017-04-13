package com.dmelnyk.workinukraine.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerApplicationComponent;
import com.dmelnyk.workinukraine.di.module.ApplicationModule;
import com.dmelnyk.workinukraine.services.WakeLockBroadcastReceiver;

import javax.inject.Inject;

import static android.content.Context.ALARM_SERVICE;
import static com.dmelnyk.workinukraine.mvp.dialog_downloading.DialogDownloadPresenter.REQUEST_CODE;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_CITY;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_REQUEST;

/**
 * Created by dmitry on 07.04.17.
 */

public class RepeatingSearch {

    private static final String TAG = "GT.RepeatingSearch";
    private final SharedPreferences preferences;
    private String requestText;
    private String requestCity;
    private String periodPreference;
    private boolean turnOnRepeating;
    private Context context;

    @Inject
    SharedPreferences sharedPreferences;

    public RepeatingSearch(Context context) {
        this.context = context;
        injectDependency(context);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void injectDependency(Context context) {
        DaggerApplicationComponent.builder().
                applicationModule(new ApplicationModule(MyApplication.get(context)))
                .build()
                .inject(this);
    }

    public void createRepeating() {
        requestText = sharedPreferences.getString(Tags.SH_PREF_REQUEST_KEY_WORDS, "");
        requestCity = sharedPreferences.getString(Tags.SH_PREF_REQUEST_CITY, "");
        periodPreference = preferences.getString("key_sound_period", "");
        turnOnRepeating = preferences.getBoolean("key_sound_switcher", true);

        Log.d(TAG, "requestText = " + requestText);
        Log.d(TAG, "periodPreference = " + periodPreference);

        Intent intent = new Intent(context, WakeLockBroadcastReceiver.class);

        intent.putExtra(KEY_CITY, requestCity);
        intent.putExtra(KEY_REQUEST, requestText);

        PendingIntent pending = PendingIntent.getBroadcast(context,
                REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long period = getPeriodInLong(); //2 * AlarmManager.INTERVAL_HALF_HOUR;

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (turnOnRepeating) {
            Log.d(TAG, "starting AlarmManager");
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, period, period, pending);
        } else {
            Log.d(TAG, "stopping AlarmManager");
            am.cancel(pending);
        }
    }

    private long getPeriodInLong() {
        String[] arrayPeriodString = context.getResources().getStringArray(R.array.settings_message_period);
        int[] arrayPeriodNumeric = context.getResources().getIntArray(R.array.settings_period_number); // 30, 1, 3, 6, 12, 24
        int period = 3;

        for (int i = 0; i < arrayPeriodString.length; i++) {
            if (periodPreference.equals(arrayPeriodString[i])) {
                period = arrayPeriodNumeric[i];
            }
        }

        Log.d(TAG, "period = " + period);

        long interval = AlarmManager.INTERVAL_HALF_HOUR; // interval of periodic intent
        if (period == 30) {
            return interval;
        } else {
            return interval * 2 * period;
        }
    }
}
