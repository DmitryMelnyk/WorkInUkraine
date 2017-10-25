package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;

import com.dmelnyk.workinukraine.job.RepeatingSearchJob;
import com.dmelnyk.workinukraine.job.AppJobCreator;
import com.dmelnyk.workinukraine.ui.settings.repository.SettingsRepository;
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

        // getting periodic search time
//        SettingsRepository repository = new SettingsRepository(this);
//        RepeatingSearchJob.scheduleRepeatingSearch(repository.getPeriodInMillis());
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
