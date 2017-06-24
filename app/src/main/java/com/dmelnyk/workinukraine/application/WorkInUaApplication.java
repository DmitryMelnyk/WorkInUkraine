package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;

import com.dmelnyk.workinukraine.di.component.ApplicationComponent;
import com.dmelnyk.workinukraine.di.component.DaggerApplicationComponent;

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
    }

    public ApplicationComponent getAppComponent() {
        return appComponent;
    }
}
