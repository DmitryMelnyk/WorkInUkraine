package com.dmelnyk.workinukraine.ui.vacancy.di;

import android.content.Context;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.business.vacancy.VacancyInteractor;
import com.dmelnyk.workinukraine.data.vacancy.IVacancyRepository;
import com.dmelnyk.workinukraine.data.vacancy.VacancyRepository;
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
    private final Context context;

    public VacancyModule(String request, Context context) {
        this.request = request;
        this.context = context.getApplicationContext();
    }

    @Provides
    @VacancyScope
    IVacancyRepository provideIVacancyRepository(BriteDatabase db) {
        return new VacancyRepository(db, context);
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
