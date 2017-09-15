package com.dmelnyk.workinukraine.ui.vacancy_viewer.di;

import com.dmelnyk.workinukraine.business.vacancy_viewer.IVacancyViewInteractor;
import com.dmelnyk.workinukraine.business.vacancy_viewer.VacancyViewInteractor;
import com.dmelnyk.workinukraine.data.vacancy_viewer.IVacancyViewerRepository;
import com.dmelnyk.workinukraine.data.vacancy_viewer.VacancyViewerRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.Contract;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerPresenter;
import com.squareup.sqlbrite2.BriteDatabase;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Completable;

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
