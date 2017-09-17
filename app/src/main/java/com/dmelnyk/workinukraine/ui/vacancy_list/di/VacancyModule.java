package com.dmelnyk.workinukraine.ui.vacancy_list.di;

import android.content.Context;

import com.dmelnyk.workinukraine.business.vacancy_list.IVacancyListInteractor;
import com.dmelnyk.workinukraine.business.vacancy_list.VacancyListInteractor;
import com.dmelnyk.workinukraine.data.vacancy_list.IVacancyListRepository;
import com.dmelnyk.workinukraine.data.vacancy_list.VacancyListRepository;
import com.dmelnyk.workinukraine.ui.vacancy_list.Contract;
import com.dmelnyk.workinukraine.ui.vacancy_list.VacancyListPresenter;
import com.squareup.sqlbrite2.BriteDatabase;

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
    IVacancyListRepository provideIVacancyRepository(BriteDatabase db) {
        return new VacancyListRepository(db, context);
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
