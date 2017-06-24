package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dmelnyk.workinukraine.helpers.RepeatingSearch;
import com.dmelnyk.workinukraine.ui.navigation.di.NavigationComponent;
import com.dmelnyk.workinukraine.ui.navigation.di.NavigationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by dmitry on 30.03.17.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(Application app);

    SharedPreferences getSharedPreferences();

    Context getContext();

    void inject(RepeatingSearch context);

    /* ------------------------------------- */
    NavigationComponent add(NavigationModule module);
}
