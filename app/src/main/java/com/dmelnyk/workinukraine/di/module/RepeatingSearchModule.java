package com.dmelnyk.workinukraine.di.module;

import android.content.Context;

import com.dmelnyk.workinukraine.di.PerActivity;
import com.dmelnyk.workinukraine.helpers.RepeatingSearch;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 07.04.17.
 */

@PerActivity
@Module
public class RepeatingSearchModule {

    @Provides static RepeatingSearch provideRepeatingSearch(Context context) {
        return new RepeatingSearch(context);
    }
}
