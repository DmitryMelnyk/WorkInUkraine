package com.dmelnyk.workinukraine.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.di.component.DbComponent;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity;
import com.dmelnyk.workinukraine.helpers.Job;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.dmelnyk.workinukraine.services.GetDataIntentService.*;

/**
 * Created by dmitry on 30.03.17.
 */

public class WakeLockBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GT.WakeLockBr";

    @Inject
    JobPool jobPool;

    public WakeLockBroadcastReceiver() { }

    private DbComponent dbComponent;

    private Context context;

    private void getDbComponent(Context context) {
        if (dbComponent == null) {
            dbComponent = DaggerDbComponent.builder()
                    .applicationComponent(MyApplication.get(context).getAppComponent())
                    .build();
            dbComponent.inject(this);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        Log.d(TAG, "onReceive()");
        getDbComponent(context);

        String requestCity = intent.getStringExtra(KEY_CITY);
        String requestKeyWords = intent.getStringExtra(KEY_REQUEST);

        giveHandler(broadcastHandler);
        Intent searchJobsIntent = new Intent(context, GetDataIntentService.class);
        searchJobsIntent.putExtra(KEY_CITY, requestCity);
        searchJobsIntent.putExtra(KEY_REQUEST, requestKeyWords);
        searchJobsIntent.putExtra(KEY_MODE, Mode.SERVICE);
        WakefulBroadcastReceiver.startWakefulService(context, searchJobsIntent);
    }

    Handler broadcastHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage() in broadcastHandler");
//            super.handleMessage(msg);
            if (msg.what == GetDataIntentService.FINISH_CODE) {
                Bundle jobs = (Bundle) msg.obj;

                ArrayList<Job> fresh = retrieveNewJobs(jobs);
                if(fresh.size() > 0) {

                    // add new vacancies to "RECENT" table
                    jobPool.addJobs(fresh, JobDbSchema.JobTable.RECENT);

                    // refresh vacancies in "NEW" table
                    jobPool.clearTable(JobDbSchema.JobTable.NEW);
                    jobPool.addJobs(fresh, JobDbSchema.JobTable.NEW);

                    showNotification(fresh);

                    // update old data in "NAMES" tables
                    jobPool.writeAllJobs(jobs);
                }
            }
        }
    };

    private ArrayList<Job> retrieveNewJobs(Bundle jobs) {
        Bundle fetchedJobs = new Bundle(jobs);
        Log.d(TAG, "retrieveNewJobs()");
        ArrayList<Job> newJobs = new ArrayList<>();

        for (String table : JobDbSchema.JobTable.NAMES) {
            ArrayList<Job> oldJobs = jobPool.getJobs(table);
            ArrayList<Job> freshJobs = new ArrayList<>(fetchedJobs.getParcelableArrayList(table));

            // remove old Jobs from fresh jobs list
            if (freshJobs != null) {
                for (Job job : oldJobs) {
                    freshJobs.remove(job);
                }
                newJobs.addAll(freshJobs);
                Log.d(TAG, "freshJobs() = " + freshJobs);
            }
        }

        Log.d(TAG, "new jobs: " + newJobs);
        return newJobs;
    }

    private void showNotification(ArrayList<Job> jobs) {
        NotificationCompat.InboxStyle style =
                new NotificationCompat.InboxStyle();

        // InboxStyle notification can contain maximum 7 lines
        for (int i = 0; i < Math.min(7, jobs.size()); i++) {
            style.addLine(jobs.get(i).getTitle());
        }

        String title = context.getString(R.string.notification_title);
        String summary = context.getString(R.string.notification_summary) + " " + jobs.size();

        style.setBigContentTitle(title);
        style.setSummaryText(summary);

        final Bitmap largeExpandedAvatar = BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(summary)
                .setSmallIcon(R.drawable.ic_work_black_24dp)
                .setLargeIcon(largeExpandedAvatar)
                .setStyle(style)
                .setContentIntent(createPendingIntent())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(NotificationManagerCompat.IMPORTANCE_DEFAULT, notification);
    }

    @NonNull
    private PendingIntent createPendingIntent() {
        Intent intent = new Intent(context, BaseActivity.class);
        intent.putExtra(BaseActivity.ACTIVITY_TYPE, BaseActivity.ActivityType.NEW);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}


