package com.dmelnyk.workinukraine.business.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.model.vacancy.IVacancyRepository;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyInteractor implements IVacancyInteractor {

    private final IVacancyRepository repository;

    public VacancyInteractor(IVacancyRepository repository) {
        this.repository = repository;
    }


    @Override
    public Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request) {
        return repository.getAllVacancies(request);
    }

    @Override
    public Observable<List<VacancyModel>> getVacancies(String request, @VacancyResource String table) {
        return repository.getVacancies(request, table);
    }

    // TODO getAllVacancies

    @Override
    public Completable onPopupMenuClicked(VacancyModel vacancy,
                                          @VacancyCardViewAdapter.VacancyPopupMenuType int operation) {
        switch (operation) {
            case VacancyCardViewAdapter.MENU_REMOVE:
                return repository.removeFromFavorites(vacancy);
            case VacancyCardViewAdapter.MENU_SAVE:
                return repository.saveToFavorite(vacancy);
        }

        return Completable.error(new Throwable("Error happened!"));
    }
}
