package com.dmelnyk.workinukraine.services.alarm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.repeating_search_service.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.SearchVacanciesService;
import com.dmelnyk.workinukraine.services.alarm.di.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.services.alarm.di.RepeatingSearchModule;
import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by d264 on 6/17/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Inject
    IRepeatingSearchRepository repository;

    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("Alarm receiver starts!");
        Log.e("999", "alarm receiver started!");
        mContext = context;

        DaggerRepeatingSearchComponent.builder()
                .dbModule(new DbModule(context))
                .repeatingSearchModule(new RepeatingSearchModule())
                .build()
                .inject(this);

        registerSearchBroadcastReceiver(context);
        // Starting searching service
        startSearchVacanciesService(context);
    }

    private void registerSearchBroadcastReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SearchVacanciesService.ACTION_FINISHED_REPEATING_SEARCH);
        LocalBroadcastManager.getInstance(context).registerReceiver(
                mDownloadingBroadcastReceiver, intentFilter);
    }

    private void startSearchVacanciesService(Context context) {
        Intent searchService = new Intent(context, SearchVacanciesService.class);
        // default (without keys) triggers background searching
        context.startService(searchService);
    }

    private final BroadcastReceiver mDownloadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                // downloading finished
                case SearchVacanciesService.ACTION_FINISHED_REPEATING_SEARCH:
                    // Unregister receiver
                    LocalBroadcastManager.getInstance(context)
                            .unregisterReceiver(mDownloadingBroadcastReceiver);
                    // Finding new vacancies
                    checkNewVacancies();
            }
        }
    };

    private void checkNewVacancies() {
        repository.getNewVacancies()
                .subscribe(newVacancies -> {
                    sendNotification(newVacancies.size());
                }, throwable -> Timber.e("Error happened", throwable));
    }

    private void sendNotification(int vacanciesFound) {
        final Bitmap largeExpandedAvatar = BitmapFactory.decodeResource(
                mContext.getResources(), R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText("Found vacancies=" + vacanciesFound)
                .setSmallIcon(R.drawable.vacancy_standard_blue)
                .setLargeIcon(largeExpandedAvatar)
                .setContentIntent(createPendingIntent(mContext))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
        nm.notify(NotificationManagerCompat.IMPORTANCE_DEFAULT, notification);
    }

    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
