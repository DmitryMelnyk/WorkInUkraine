package com.dmelnyk.workinukraine.ui.search.di;

import com.dmelnyk.workinukraine.ui.search.SearchFragment;

import dagger.Subcomponent;

/**
 * Created by d264 on 6/11/17.
 */

@Subcomponent(modules = SearchModule.class)
@SearchScope
public interface SearchComponent {
    void inject(SearchFragment fragment);
}
