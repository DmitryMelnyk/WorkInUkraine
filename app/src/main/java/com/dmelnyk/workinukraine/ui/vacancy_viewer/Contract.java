package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import com.dmelnyk.workinukraine.models.VacancyModel;

/**
 * Created by d264 on 9/14/17.
 */

public class Contract {

    public interface IVacancyViewerView {
        void showUpdatingVacancySuccess(Boolean isFavorite);
        void showUpdatingVacancyError();
    }

    public interface IVacancyViewerPresenter {
        void updateFavoriteStatusVacancy(VacancyModel vacancy);
        void bindView(IVacancyViewerView view);
        void unbindView();
    }
}
