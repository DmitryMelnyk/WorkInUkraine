package com.dmelnyk.workinukraine.ui.vacancy_list.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.vacancy_list.VacancyListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 7/29/17.
 */

@Component(modules = { VacancyModule.class, DbModule.class })
@Singleton
public interface VacancyComponent {
    void inject(VacancyListActivity activity);
}
