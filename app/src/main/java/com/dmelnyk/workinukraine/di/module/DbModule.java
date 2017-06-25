package com.dmelnyk.workinukraine.di.module;

import android.content.Context;

import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.application.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 30.03.17.
 */

@Module
public class DbModule {

    @ApplicationScope
    @Provides
    JobPool provideJobPool(Context context) {
        return new JobPool(context);
    }
}
