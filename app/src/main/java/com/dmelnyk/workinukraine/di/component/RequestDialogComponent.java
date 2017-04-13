package com.dmelnyk.workinukraine.di.component;

import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequest;
import com.dmelnyk.workinukraine.di.PerActivity;
import com.dmelnyk.workinukraine.di.module.CityModule;
import com.dmelnyk.workinukraine.di.module.DbModule;
import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequestPresenter;

import dagger.Component;

/**
 * Created by dmitry on 30.03.17.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class,
        modules = {DbModule.class, CityModule.class}
)
public interface RequestDialogComponent {

    void inject(DialogRequestPresenter presenter);
}
