package com.dmelnyk.workinukraine.ui.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;

import java.util.List;

/**
 * Created by d264 on 7/28/17.
 */

public class Contract {
    public interface IVacancyView {
        void onChangeData(List<VacancyModel> vacancies);

        void openVacancyInWeb(String url);

        void createShareIntent(VacancyModel vacancy);

        void showResultingMessage(@CardViewAdapter.VacancyPopupMenuType int type);

        void showErrorMessage(String message);

        void displayLoadingProcess();

        void displayFragment(String type, List<VacancyModel> vacanciesList);

        void hideLoadingProcess();
    }

    public interface IVacancyPresenter {
        void bindView(IVacancyView view, String request, String table);

        void unbindView();

        void onItemPopupMenuClicked(String request, VacancyModel vacancy,
                                    @CardViewAdapter.VacancyPopupMenuType int type);
    }
}
