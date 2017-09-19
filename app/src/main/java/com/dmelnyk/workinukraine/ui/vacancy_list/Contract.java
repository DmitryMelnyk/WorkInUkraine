package com.dmelnyk.workinukraine.ui.vacancy_list;

import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/28/17.
 */

public class Contract {
    public interface IVacancyView {
        void createShareIntent(VacancyModel vacancy);

        void showResultingMessage(@VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void showErrorMessage(String message);

        void updateFavoriteTab(List<VacancyModel> vacancies);

        void displayTabFragment(
                String[] tabTitles,
                int[] tabVacancyCount,
                int isButtonTubWithNewIcon,
                Map<String, List<VacancyModel>> allVacancies);

        void exitActivity();
    }

    public interface IVacancyPresenter {
        void bindView(IVacancyView view, String request);

        void bindJustView(IVacancyView view);

        void unbindView();

        void onItemPopupMenuClicked(
                VacancyModel vacancy, @VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void clear();
    }
}
