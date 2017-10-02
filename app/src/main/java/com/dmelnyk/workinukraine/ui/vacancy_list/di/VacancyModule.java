package com.dmelnyk.workinukraine.ui.vacancy_list.di;

import android.content.Context;

import com.dmelnyk.workinukraine.ui.vacancy_list.business.IVacancyListInteractor;
import com.dmelnyk.workinukraine.ui.vacancy_list.business.VacancyListInteractor;
import com.dmelnyk.workinukraine.ui.vacancy_list.repository.IVacancyListRepository;
import com.dmelnyk.workinukraine.ui.vacancy_list.repository.VacancyListRepository;
import com.dmelnyk.workinukraine.ui.vacancy_list.Contract;
import com.dmelnyk.workinukraine.ui.vacancy_list.VacancyListPresenter;
import com.dmelnyk.workinukraine.utils.SharedPrefUtil;
import com.squareup.sqlbrite2.BriteDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 7/28/17.
 */

@Module
public class VacancyModule {

    private final String request;
    private final Context context;

    public VacancyModule(String request, Context context) {
        this.request = request;
        this.context = context.getApplicationContext();
    }

    @Provides
    @VacancyScope
    IVacancyListRepository provideIVacancyRepository(BriteDatabase db, SharedPrefUtil sharedPrefUtil) {
        return new VacancyListRepository(db, context, sharedPrefUtil);
    }

    @Provides
    @VacancyScope
    IVacancyListInteractor provideIVacancyInteractor(IVacancyListRepository repository) {
        return new VacancyListInteractor(repository);
    }

    @Provides
    @VacancyScope
    Contract.IVacancyPresenter providePresenter(IVacancyListInteractor interactor) {
        return new VacancyListPresenter(interactor, request);
    }
}
