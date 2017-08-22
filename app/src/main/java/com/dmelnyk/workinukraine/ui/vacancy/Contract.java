package com.dmelnyk.workinukraine.ui.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/28/17.
 */

public class Contract {
    public interface IVacancyView {
        void openVacancyInWeb(String url);

        void createShareIntent(VacancyModel vacancy);

        void showResultingMessage(@VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void showErrorMessage(String message);

        void displayLoadingProcess();

        void hideLoadingProcess();

        void displayTabFragment(Map<String, Map<String, List<VacancyModel>>> vacanciesMap);

        void updateFavoriteTab(List<VacancyModel> vacancies);
    }

    public interface IVacancyPresenter {
        void bindView(IVacancyView view, String request);

        void unbindView();

        void onItemPopupMenuClicked(
                VacancyModel vacancy, @VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void clear();
    }
}
