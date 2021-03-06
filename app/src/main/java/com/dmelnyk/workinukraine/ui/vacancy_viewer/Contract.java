package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.List;

/**
 * Created by d264 on 9/14/17.
 */

public class Contract {

    public interface IVacancyViewerView {
        void displayVacancies(List<VacancyModel> vacancies);

        void showUpdatingVacancySuccess(Boolean isFavorite);
        void showUpdatingVacancyError();

        void hideNoConnection();

        void showNoConnection();

        boolean isConnected();
    }

    public interface IVacancyViewerPresenter {
        void updateFavoriteStatusVacancy(VacancyModel vacancy);
        void onCreate(IVacancyViewerView view);

        void getData(String request, String type, @Nullable String site);

        void onStop();

        void onDestroy();

        void onInternetStatusChanged(boolean isConnected);

        void onResume();
    }
}
