package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.job.AppJobCreator;
import com.dmelnyk.workinukraine.ui.settings.repository.SettingsRepository;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.dmelnyk.workinukraine.utils.Tags;
import com.evernote.android.job.JobManager;

import timber.log.Timber;

/**
 * Created by dmitry on 30.03.17.
 */


public class WorkInUaApplication extends Application {

    protected ApplicationComponent appComponent;

    public static WorkInUaApplication get(Context context) {
        return (WorkInUaApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        appComponent.inject(this);

        Timber.plant(new Timber.DebugTree(){
            @Override
            protected String createStackElementTag(StackTraceElement element) {
                return element.getLineNumber() + ": " + element;
            }
        });

        JobManager.create(this).addJobCreator(new AppJobCreator());

        // starts periodic search only once
        runRepeatingSearch();
    }

    private void runRepeatingSearch() {
        if (!SharedPrefUtil.isRepeatingSearchRunning(this)) {
            Log.d(getClass().getSimpleName(), "Creating repeating search Scheduler task");
            Log.e(Tags.REPEATING_SEARCH, "Creating repeating search Scheduler task");
            // getting periodic search time
            SettingsRepository repository = new SettingsRepository(this);
            RepeatingSearchJob.scheduleRepeatingSearch(repository.getPeriodInMillis());
            SharedPrefUtil.setRepeatingSearchStarted(this);
        } else {
            Log.d(Tags.REPEATING_SEARCH, "Repeating task is already started.");
        }
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
