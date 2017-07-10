package com.dmelnyk.workinukraine.ui.settings.di;

import com.dmelnyk.workinukraine.ui.settings.SettingsFragment;

import dagger.Subcomponent;

/**
 * Created by d264 on 6/11/17.
 */

@Subcomponent(modules = SettingsModule.class)
@SettingsScope
public interface SettingsComponent {
    void inject(SettingsFragment fragment);
}
