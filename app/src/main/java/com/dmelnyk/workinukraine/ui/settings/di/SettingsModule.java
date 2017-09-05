package com.dmelnyk.workinukraine.ui.settings.di;

import android.content.Context;

import com.dmelnyk.workinukraine.business.settings.ISettingsInteractor;
import com.dmelnyk.workinukraine.business.settings.SettingsInteractor;
import com.dmelnyk.workinukraine.data.settings.ISettingsRepository;
import com.dmelnyk.workinukraine.data.settings.SettingsRepository;
import com.dmelnyk.workinukraine.ui.settings.Contract.ISettingsPresenter;
import com.dmelnyk.workinukraine.ui.settings.SettingsPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 6/11/17.
 */

@Module
public class SettingsModule {

    @Provides
    @SettingsScope
    ISettingsRepository providesISettingsRepository(Context context) {
        return new SettingsRepository(context);
    }

    @Provides
    @SettingsScope
    ISettingsInteractor providesISearchInteractor(ISettingsRepository repository) {
        return new SettingsInteractor(repository);
    }

    @Provides
    @SettingsScope
    ISettingsPresenter providesISearchPresenter(ISettingsInteractor interactor) {
        return new SettingsPresenter(interactor);
    }

}
