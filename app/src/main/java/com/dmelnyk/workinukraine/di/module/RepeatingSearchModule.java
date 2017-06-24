package com.dmelnyk.workinukraine.di.module;

import android.content.Context;

import com.dmelnyk.workinukraine.application.ApplicationScope;
import com.dmelnyk.workinukraine.helpers.RepeatingSearch;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 07.04.17.
 */

@ApplicationScope
@Module
public class RepeatingSearchModule {

    @Provides static RepeatingSearch provideRepeatingSearch(Context context) {
        return new RepeatingSearch(context);
    }
}
