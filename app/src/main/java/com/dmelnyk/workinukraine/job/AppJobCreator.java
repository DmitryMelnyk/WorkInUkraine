package com.dmelnyk.workinukraine.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.job.SearchVacanciesJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;

/**
 * Created by d264 on 10/16/17.
 */

public class AppJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
//            case SearchVacanciesJob.TAG:
//                return new SearchVacanciesJob();
            case RepeatingSearchJob.TAG:
                return new RepeatingSearchJob();
            default:
                return null;
        }
    }

    public static void clearAllTasks() {
        Log.d(AppJobCreator.class.getSimpleName(), "clearing all tasks.");
        JobManager.instance().cancelAllForTag(RepeatingSearchJob.TAG);
    }
}
