package com.dmelnyk.workinukraine.job;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.periodic_search.di.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.services.periodic_search.di.RepeatingSearchModule;
import com.dmelnyk.workinukraine.services.periodic_search.repo.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.services.search.SearchVacanciesService;
import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Calendar;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by d264 on 10/16/17.
 */

public class RepeatingSearchJob extends Job {
    public static final String TAG = "RepeatingSearchJob_TAG";
    private static int jobId;

    private Context mContext;

    public RepeatingSearchJob() {
        super();
    }

    @Inject
    IRepeatingSearchRepository repository;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        Log.e("!!!", "RepeatingSearchJob.onRunJob()");

        mContext = getContext();
        DaggerRepeatingSearchComponent.builder()
                .dbModule(new DbModule(mContext))
                .repeatingSearchModule(new RepeatingSearchModule())
                .build()
                .inject(this);

        if (!repository.isSleepModeEnabled()) {
            JobManager.instance().cancel(jobId);
        }

        if (repository.getRequestCount() == 0) {
            // don't start repeating alarm if there is no request
            Timber.d("999", "repeating alarm doesn't start because of empty request list");
            return Result.SUCCESS;
        }

        // check i is time to turn of service. In that case starts morning alarm
        // Starts repeating service if it is enable in settings and there is time
        // before sleep.
        if (repository.getSleepFromTime().after(repository.getCurrentTime())) {
            // Starting searching service
            registerSearchBroadcastReceiver(mContext);
            startSearchVacanciesService(mContext);
            scheduleRepeatingSearch(repository.getUpdateInterval());
        } else {
            Timber.d("Vacancy service stopped! Time to sleep!");
            Log.e("ALARM", "Vacancy service stopped! Time to sleep!");
            // Starts wake alarm
            long wakeUpTime = getMorningAlarm(
                    repository.getCurrentTime(),
                    repository.getWakeTime());

            scheduleSearchAtTime(wakeUpTime);
        }

        return Result.SUCCESS;
    }

    public static void scheduleRepeatingSearch(long interval) {
        Timber.d("Interval=" + interval);

        jobId = new JobRequest.Builder(TAG)
                .setPeriodic(interval)
                .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    // schedule task to run in the morning
    private void scheduleSearchAtTime(long wakeUpTime) {
        new JobRequest.Builder(TAG)
                .setPeriodic(wakeUpTime)
                .setRequiredNetworkType(JobRequest.NetworkType.UNMETERED)
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    // Check if wake time is in the past. Then add 1 day.
    private long getMorningAlarm(Calendar calendarNow, Calendar calendarWakeTime) {
        if (calendarWakeTime.before(calendarNow)) {
            // if time is in the past add 1 day
            calendarWakeTime.add(Calendar.DATE, 1);
        }

        return calendarWakeTime.getTimeInMillis();
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
                    // Shows notification if you've found new vacancies
                    Timber.d("Found vacancies count=" + newVacancies.size());

                    int previousNewVacanciesCount = repository.getPreviousNewVacanciesCount();
                    Timber.d("Previous new vacancies count=" + previousNewVacanciesCount);
                    if (!newVacancies.isEmpty()
                            && newVacancies.size() != previousNewVacanciesCount) {
                        sendNotification(newVacancies.size());
                        repository.saveNewVacanciesCount(newVacancies.size());
                        Timber.d("new vacancies " + newVacancies.size());
                    } else {
                        Timber.d("no new vacancies!");
                    }

                    // Closing database
                    repository.close();
                }, throwable -> Timber.e("Error happened", throwable));
    }

    private void sendNotification(int vacanciesFound) {
        final Bitmap largeExpandedAvatar = BitmapFactory.decodeResource(
                mContext.getResources(), R.mipmap.ic_launcher);

        boolean isVibroEnable = repository.isVibroEnable();
        boolean isSoundEnable = repository.isSoundEnable();

        // for Version >= 26
        initChannels(mContext);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, "default")
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText("Найдено новых вакансий: " + vacanciesFound)
                .setSmallIcon(R.drawable.ic_work_black_24dp)
                .setLargeIcon(largeExpandedAvatar)
                .setContentIntent(createPendingIntent(mContext))
                .setAutoCancel(true);

        if (isVibroEnable ) {
            notification.setVibrate(new long[]{1000, 1000});
        }

        if (isSoundEnable) {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.setSound(notificationSound);
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
        nm.notify(NotificationManagerCompat.IMPORTANCE_DEFAULT, notification.build());
    }


    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }


    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
