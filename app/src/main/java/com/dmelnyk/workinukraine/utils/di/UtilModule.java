package com.dmelnyk.workinukraine.utils.di;

import android.content.Context;

import com.dmelnyk.workinukraine.utils.ImageUtils;
import com.dmelnyk.workinukraine.utils.CityUtils;
import com.dmelnyk.workinukraine.utils.NetUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dmitry on 28.03.17.
 */

@Module
public class UtilModule {

    @Provides
    ImageUtils provideImageUtils() {
        return new ImageUtils();
    }

    @Provides
    NetUtils provideNetUtils() {
        return NetUtils.getInstance();
    }

    @Provides
    CityUtils provideCityUtils(Context context) { return new CityUtils(context);
    }
}
