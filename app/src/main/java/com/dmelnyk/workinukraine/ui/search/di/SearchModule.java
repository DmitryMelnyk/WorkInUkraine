package com.dmelnyk.workinukraine.ui.search.di;


import android.content.Context;

import com.dmelnyk.workinukraine.ui.search.business.ISearchInteractor;
import com.dmelnyk.workinukraine.ui.search.business.SearchInteractor;
import com.dmelnyk.workinukraine.ui.search.repository.ISearchRepository;
import com.dmelnyk.workinukraine.ui.search.repository.SearchRepository;
import com.dmelnyk.workinukraine.ui.search.Contract;
import com.dmelnyk.workinukraine.ui.search.SearchPresenter;
import com.dmelnyk.workinukraine.utils.SharedPrefFilterUtil;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.squareup.sqlbrite2.BriteDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 6/11/17.
 */

@Module
public class SearchModule {

    @Provides
    @SearchScope
    ISearchRepository providesISearchRepository(
            BriteDatabase bd, SharedPrefUtil sharedPrefUtil, SharedPrefFilterUtil filterUtil) {
        return new SearchRepository(bd, sharedPrefUtil, filterUtil);
    }

    @Provides
    @SearchScope
    ISearchInteractor providesISearchInteractor(ISearchRepository repository) {
        return new SearchInteractor(repository);
    }

    @Provides
    @SearchScope
    Contract.ISearchPresenter providesISearchPresenter(ISearchInteractor interactor) {
        return new SearchPresenter(interactor);
    }

}
