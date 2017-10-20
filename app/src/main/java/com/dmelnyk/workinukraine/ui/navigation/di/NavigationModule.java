package com.dmelnyk.workinukraine.ui.navigation.di;

import android.content.Context;

import com.dmelnyk.workinukraine.ui.navigation.repository.INavigationRepository;
import com.dmelnyk.workinukraine.ui.navigation.repository.NavigationRepository;
import com.dmelnyk.workinukraine.services.periodic_search.AlarmClockUtil;
import com.dmelnyk.workinukraine.ui.navigation.Contract;
import com.dmelnyk.workinukraine.ui.navigation.NavUtil;
import com.dmelnyk.workinukraine.ui.navigation.NavigationPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 6/11/17.
 */

@Module
public class NavigationModule {

    @Provides
    @NavigationScope
    INavigationRepository providesITimeRepository(Context context) {
        return new NavigationRepository(context);
    }

    @Provides
    @NavigationScope
    Contract.INavigationPresenter providesINavigationPresenter(INavigationRepository repository) {
        return new NavigationPresenter(repository);
    }

    @Provides
    @NavigationScope
    NavUtil providesNavUtils(Context context) {
        return new NavUtil(context);
    }

    @Provides
    @NavigationScope
    AlarmClockUtil providesAlarmClockUtil(Context context) {
        return new AlarmClockUtil(context);
    }
}
