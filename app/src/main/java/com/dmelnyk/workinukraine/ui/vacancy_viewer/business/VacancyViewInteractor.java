package com.dmelnyk.workinukraine.ui.vacancy_viewer.business;

import android.support.annotation.Nullable;
import android.util.Log;

import com.dmelnyk.workinukraine.ui.vacancy_viewer.repository.IVacancyViewerRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void clear() {
        repository.close();
    }

    @Override
    public Single<List<VacancyModel>> getVacancies(String request, String type, @Nullable String site) {
        switch (type) {
            case VacancyViewerActivity.DATA_FAVORITE:
                return repository.getFavoriteVacancies(request).map(list -> filterVacancies(list, request));
            case VacancyViewerActivity.DATA_NEW:
                return repository.getNewVacancies(request).map(list -> filterVacancies(list, request));
            case VacancyViewerActivity.DATA_RECENT:
                return repository.getRecentVacancies(request).map(list -> filterVacancies(list, request));
            case VacancyViewerActivity.DATA_SITE:
                return repository.getSiteVacancies(request, site).map(list -> filterVacancies(list, request));
        }

        // never happen
        return null;
    }

    @Override
    public Single<Boolean> updateFavorite(VacancyModel vacancy) {
        return repository.updateFavorite(vacancy);
    }

    private List<VacancyModel> filterVacancies(List<VacancyModel> list, String request) {
        boolean isFilterEnable = repository.isFilterEnable(request);
        if (!isFilterEnable) return list;

        Set<String> filters = repository.getFilterWords(request);

        filters.add("senior");
        filters.add("php");

        if (!list.isEmpty() && !filters.isEmpty()) {
            List<VacancyModel> filteredVacancies = new ArrayList<>(list);

            for (VacancyModel vacancy : list) {
                for (String filter : filters) {
                    boolean vacancyContainsForbiddenWord =
                            vacancy.title().toLowerCase().contains(filter.toLowerCase());
                    Log.e("2222", "filter=" + filter + " vacancy=" + vacancy.title()
                            + "| vacancy contains forbidden=" + vacancyContainsForbiddenWord );

                    if (vacancyContainsForbiddenWord) {
                        filteredVacancies.remove(vacancy);
                        break;
                    }
                }
            }

            return filteredVacancies;
        } else {
            return list;
        }
    }
}
