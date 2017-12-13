package com.dmelnyk.workinukraine.services.periodic_search.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.job.RepeatingSearchJob;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by d264 on 9/5/17.
 */

@Component(
        modules = {DbModule.class, RepeatingSearchModule.class})

@Singleton
public interface RepeatingSearchComponent {
    void inject(RepeatingSearchJob repeatingSearchJob);
}
