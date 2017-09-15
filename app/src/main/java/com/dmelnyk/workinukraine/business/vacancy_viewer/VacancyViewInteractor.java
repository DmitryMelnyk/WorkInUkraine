package com.dmelnyk.workinukraine.business.vacancy_viewer;

import com.dmelnyk.workinukraine.data.vacancy_viewer.IVacancyViewerRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;

import io.reactivex.Single;

/**
 * Created by d264 on 9/14/17.
 */

public class VacancyViewInteractor implements IVacancyViewInteractor {

    private final IVacancyViewerRepository repository;

    public VacancyViewInteractor(IVacancyViewerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        return repository.updateFavorite(vacancy);
    }
}
