package com.dmelnyk.workinukraine.db.di;

import com.dmelnyk.workinukraine.services.SearchVacanciesService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 7/25/17.
 */

@Component(modules = DbModule.class)
@Singleton
public interface DbComponent {
    void inject(SearchVacanciesService service);
}
