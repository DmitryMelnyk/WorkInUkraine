package com.dmelnyk.workinukraine.di.component;

import com.dmelnyk.workinukraine.di.PerActivity;
import com.dmelnyk.workinukraine.di.module.RepeatingSearchModule;
import com.dmelnyk.workinukraine.mvp.activity_settings.FragmentSettingsPresenter;
import com.dmelnyk.workinukraine.mvp.dialog_downloading.DialogDownloadPresenter;

import dagger.Component;

/**
 * Created by dmitry on 07.04.17.
 */

@PerActivity
@Component(
        dependencies = ApplicationComponent.class,
        modules = RepeatingSearchModule.class)
public interface RepeatingSearchComponent {

    void inject(FragmentSettingsPresenter presenter);

    void inject(DialogDownloadPresenter presenter);
}
