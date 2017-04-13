package com.dmelnyk.workinukraine.di.module;

import android.content.Context;

import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.PerActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 30.03.17.
 */

@Module
public class DbModule {

    @PerActivity
    @Provides
    JobPool provideJobPool(Context context) {
        return new JobPool(context);
    }
}
