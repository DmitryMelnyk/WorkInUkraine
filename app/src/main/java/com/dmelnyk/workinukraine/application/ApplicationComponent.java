package com.dmelnyk.workinukraine.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.dmelnyk.workinukraine.ui.navigation.di.NavigationComponent;
import com.dmelnyk.workinukraine.ui.navigation.di.NavigationModule;
import com.dmelnyk.workinukraine.ui.settings.di.SettingsComponent;
import com.dmelnyk.workinukraine.ui.settings.di.SettingsModule;

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

    /* ------------------------------------- */
    NavigationComponent add(NavigationModule module);

    SettingsComponent add(SettingsModule module);
}
