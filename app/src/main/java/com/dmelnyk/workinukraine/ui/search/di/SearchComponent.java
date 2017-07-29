package com.dmelnyk.workinukraine.ui.search.di;

import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.search.SearchFragment;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

/**
 * Created by d264 on 6/11/17.
 */

@Component(modules = { SearchModule.class, DbModule.class })
@Singleton
public interface SearchComponent {
    void inject(SearchFragment fragment);
}
