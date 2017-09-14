package com.dmelnyk.workinukraine.ui.vacancy_viewer.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 9/14/17.
 */

@Component(modules = { VacancyViewerModule.class, DbModule.class })
@Singleton
public interface VacancyViewerComponent {

    void inject(VacancyViewerActivity activity);
}
