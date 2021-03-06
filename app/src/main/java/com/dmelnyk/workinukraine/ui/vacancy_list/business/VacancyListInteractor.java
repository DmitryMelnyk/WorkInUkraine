package com.dmelnyk.workinukraine.ui.vacancy_list.business;

import android.util.Pair;

import com.dmelnyk.workinukraine.ui.vacancy_list.repository.IVacancyListRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

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
    public Single<Map<String, List<VacancyModel>>> getAllVacancies(String request) {
        return repository.getAllVacancies(request);
    }

    @Override
    public Observable<List<VacancyModel>> getFavoriteVacancies(String request) {
        return repository.getFavoriteVacancies(request);
    }

    @Override
    public String[] getTitles(String type) {
        switch (type) {
            case TITLE_NEW:
                return repository.getNewTitles();
            case TITLE_NEW_AND_RECENT:
                return repository.getNewAndRecent();
            case TITLE_RECENT:
                return repository.getRecentTitles();
        }

        return null;
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

    @Override
    public void onVacanciesViewed(String request) {
        repository.updateTimeStatusVacancies(request);
    }

    @Override
    public Pair<Boolean, Set<String>> getFilterItems() {
        return new Pair<>(isFilterEnable(), getFilterWords());
    }

    @Override
    public Completable updateFilter(Pair<Boolean, Set<String>> data) {
        return Completable.fromCallable(() -> {
            repository.setIsFilterEnable(data.first);
            repository.saveFilterWords(data.second);
            return null;
        });
    }

    @Override
    public Single<Map<String, List<VacancyModel>>> getFilteredVacancies() {
        return repository.getFilteredVacancies();
    }

    private void saveFilterWords(Set<String> words) {
        repository.saveFilterWords(words);
    }

    private void setIsFilterEnable(boolean enable) {
        repository.setIsFilterEnable(enable);
    }

    private boolean isFilterEnable() {
        return repository.isFilterEnable();
    }

    private Set<String> getFilterWords() {
        return repository.getFilterWords();
    }
}
