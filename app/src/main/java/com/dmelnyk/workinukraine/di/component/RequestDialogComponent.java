package com.dmelnyk.workinukraine.di.component;

import com.dmelnyk.workinukraine.application.ApplicationComponent;
import com.dmelnyk.workinukraine.application.ApplicationScope;
import com.dmelnyk.workinukraine.utils.di.CityModule;
import com.dmelnyk.workinukraine.ui.dialogs.request.DialogRequestPresenter;

import dagger.Component;

/**
 * Created by dmitry on 30.03.17.
 */

@ApplicationScope
@Component(dependencies = ApplicationComponent.class,
        modules = {CityModule.class}
)
public interface RequestDialogComponent {

    void inject(DialogRequestPresenter presenter);
}
