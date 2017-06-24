package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 30.03.17.
 */

@Module
public class ApplicationModule {

    private final WorkInUaApplication app;

    public ApplicationModule(WorkInUaApplication application) {
        app = application;
    }

    @Provides
    @Singleton
    Application getApplication() {
        return app;
    }

    @Provides
    @Singleton
    Context getApplicationContext() {
        return app;
    }

    @Provides
    @Singleton
    SharedPreferences getSharedPreferences() {
        return app.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }
}
