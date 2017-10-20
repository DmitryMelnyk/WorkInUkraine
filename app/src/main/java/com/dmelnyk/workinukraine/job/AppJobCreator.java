package com.dmelnyk.workinukraine.job;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.job.SearchVacanciesJob;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by d264 on 10/16/17.
 */

public class AppJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case SearchVacanciesJob.TAG:
                return new SearchVacanciesJob();
            case RepeatingSearchJob.TAG:
                return new RepeatingSearchJob();
            default:
                return null;
        }
    }
}
