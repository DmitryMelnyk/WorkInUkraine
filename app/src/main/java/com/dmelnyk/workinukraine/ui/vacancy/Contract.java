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
        void createShareIntent(VacancyModel vacancy);

        void showResultingMessage(@VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void showErrorMessage(String message);

        void updateFavoriteTab(List<VacancyModel> vacancies);

        void displayTabFragment(
                String[] tabTitles,
                int[] tabVacancyCount,
                boolean isButtonTubWithNewIcon,
                Map<String, Map<String, List<VacancyModel>>> allVacancies);

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
