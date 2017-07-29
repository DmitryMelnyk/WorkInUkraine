package com.dmelnyk.workinukraine.business.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.model.vacancy.IVacancyRepository;
import com.dmelnyk.workinukraine.ui.vacancy.CardViewAdapter;

import java.util.List;

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
    public Observable<List<VacancyModel>> getVacancies(String request, String table) {
        return repository.getVacancies(request, table);
    }

    @Override
    public Completable onPopupMenuClicked(VacancyModel vacancy,
                                          @CardViewAdapter.VacancyPopupMenuType int operation) {
        switch (operation) {
            case CardViewAdapter.REMOVE:
                return repository.removeItem(vacancy);
            case CardViewAdapter.SAVE:
                return repository.saveToFavorite(vacancy);
        }

        return Completable.error(new Throwable("Error happened!"));
    }
}
