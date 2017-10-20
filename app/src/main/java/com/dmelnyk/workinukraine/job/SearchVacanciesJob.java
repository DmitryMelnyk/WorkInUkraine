package com.dmelnyk.workinukraine.job;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.Random;

/**
 * Created by d264 on 10/16/17.
 */

public class SearchVacanciesJob extends Job {

    public static final String TAG = "SearchVacanciesJob_TAG";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0,
                new Intent(getContext(), NavigationActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(getContext(), "default")
                .setContentTitle("Android Job Demo")
                .setContentText("Notification from Android Job Demo App.")
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowWhen(true)
                .setColor(Color.RED)
                .setLocalOnly(true)
                .build();

        NotificationManagerCompat.from(getContext())
                .notify(new Random().nextInt(), notification);

        return Result.SUCCESS;
//        return null;
    }

    public static void scheduleSearchTask() {
        new JobRequest.Builder(TAG)
                .startNow()
                .build()
                .schedule();
    }
}
