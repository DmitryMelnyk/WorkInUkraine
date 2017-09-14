package com.dmelnyk.workinukraine.business.vacancy_list;

import com.dmelnyk.workinukraine.data.vacancy_list.IVacancyListRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyListInteractor implements IVacancyListInteractor {

    private final IVacancyListRepository repository;

    public VacancyListInteractor(IVacancyListRepository repository) {
        this.repository = repository;
    }

    @Override
    public void clear() {
        repository.close();
    }

    @Override
    public Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request) {
        return repository.getAllVacancies(request);
    }

    @Override
    public Observable<List<VacancyModel>> getFavoriteVacancies(String request, @VacancyResource String table) {
        return repository.getFavoriteVacancies(request, table);
    }

    @Override
    public String[] getTitles(String type) {
        if (type.equals(TITLE_NEW)) {
            return repository.getNewTitles();
        } else {
            return repository.getRecentTitles();
        }

    }

    @Override
    public Completable onPopupMenuClicked(VacancyModel vacancy,
                                          @VacancyCardViewAdapter.VacancyPopupMenuType int operation) {
        switch (operation) {
            case VacancyCardViewAdapter.MENU_REMOVE:
                return repository.removeFromFavorites(vacancy);
            case VacancyCardViewAdapter.MENU_SAVE:
                return repository.addToFavorite(vacancy);
        }

        return Completable.error(new Throwable("Error happened!"));
    }
}
