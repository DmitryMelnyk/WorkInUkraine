package com.dmelnyk.workinukraine.business.vacancy_viewer;

import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.data.vacancy_viewer.IVacancyViewerRepository;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerActivity;

import java.util.List;

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
    public Single<List<VacancyModel>> getVacancies(String request, String type, @Nullable String site) {
        switch (type) {
            case VacancyViewerActivity.DATA_FAVORITE:
                return repository.getFavoriteVacancies(request);
            case VacancyViewerActivity.DATA_NEW:
                return repository.getNewVacancies(request);
            case VacancyViewerActivity.DATA_RECENT:
                return repository.getRecentVacancies(request);
            case VacancyViewerActivity.DATA_SITE:
                return repository.getSiteVacancies(request, site);
        }

        // never happen
        return null;
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        return repository.updateFavorite(vacancy);
    }
}
