package com.dmelnyk.workinukraine.ui.vacancy.di;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.business.vacancy.VacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.model.vacancy.IVacancyRepository;
import com.dmelnyk.workinukraine.model.vacancy.VacancyRepository;
import com.dmelnyk.workinukraine.ui.vacancy.Contract;
import com.dmelnyk.workinukraine.ui.vacancy.VacancyPresenter;
import com.squareup.sqlbrite2.BriteDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 7/28/17.
 */

@Module
public class VacancyModule {

    private final String request;

    public VacancyModule(String request) {
        this.request = request;
    }

    @Provides
    @VacancyScope
    IVacancyRepository provideIVacancyRepository(BriteDatabase db) {
        return new VacancyRepository(db);
    }

    @Provides
    @VacancyScope
    IVacancyInteractor provideIVacancyInteractor(IVacancyRepository repository) {
        return new VacancyInteractor(repository);
    }

    @Provides
    @VacancyScope
    Contract.IVacancyPresenter providePresenter(IVacancyInteractor interactor) {
        return new VacancyPresenter(interactor, request);
    }
}
