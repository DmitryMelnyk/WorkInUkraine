package com.dmelnyk.workinukraine.di.module;

import android.content.Context;

import com.dmelnyk.workinukraine.helpers.CityUtils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 30.03.17.
 */


@Module
public class CityModule {

    @Provides
    CityUtils provideCityUtils(Context context) {
        return new CityUtils(context.getApplicationContext());
    }
}
