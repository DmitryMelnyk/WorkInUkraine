package com.dmelnyk.workinukraine.ui.search.di;

import android.content.Context;


import com.dmelnyk.workinukraine.business.search.ISearchInteractor;
import com.dmelnyk.workinukraine.business.search.SearchInteractor;
import com.dmelnyk.workinukraine.model.search.ISearchRepository;
import com.dmelnyk.workinukraine.model.search.SearchRepository;
import com.dmelnyk.workinukraine.ui.search.Contract;
import com.dmelnyk.workinukraine.ui.search.SearchPresenter;
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
    ISearchRepository providesISearchRepository(BriteDatabase bd) {
        return new SearchRepository(bd);
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
