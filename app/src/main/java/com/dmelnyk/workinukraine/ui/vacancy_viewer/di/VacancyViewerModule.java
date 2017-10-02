package com.dmelnyk.workinukraine.ui.vacancy_viewer.di;

import com.dmelnyk.workinukraine.ui.vacancy_viewer.business.IVacancyViewInteractor;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.business.VacancyViewInteractor;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.repository.IVacancyViewerRepository;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.repository.VacancyViewerRepository;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.Contract;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerPresenter;
import com.squareup.sqlbrite2.BriteDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by d264 on 9/14/17.
 */

@Module
public class VacancyViewerModule {

    @Provides
    @VacancyViewerScope
    IVacancyViewerRepository providesIVacancyViewerRepository(BriteDatabase db) {
        return new VacancyViewerRepository(db);
    }

    @Provides
    @VacancyViewerScope
    IVacancyViewInteractor providesIVacancyViewInteractor(IVacancyViewerRepository repository) {
        return new VacancyViewInteractor(repository);
    }

    @Provides
    @VacancyViewerScope
    Contract.IVacancyViewerPresenter providesIVacancyViewerPresenter(IVacancyViewInteractor interactor) {
        return new VacancyViewerPresenter(interactor);
    }
}
