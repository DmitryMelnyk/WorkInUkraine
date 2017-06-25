package com.dmelnyk.workinukraine.ui.navigation.di;

import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;

import dagger.Subcomponent;

/**
 * Created by d264 on 6/11/17.
 */

@Subcomponent(modules = NavigationModule.class)
@NavigationScope
public interface NavigationComponent {
    void inject(NavigationActivity activity);
}
