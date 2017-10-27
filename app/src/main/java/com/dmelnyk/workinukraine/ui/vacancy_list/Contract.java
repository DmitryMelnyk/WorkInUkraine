package com.dmelnyk.workinukraine.ui.vacancy_list;

import android.util.Pair;

import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by d264 on 7/28/17.
 */

public class Contract {
    public interface IVacancyView {
        void createShareIntent(VacancyModel vacancy);

        void showResultingMessage(@VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void showAddToFavoriteErrorMessage();

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

        void onResume(IVacancyView view);

        void onStop();

        void onItemPopupMenuClicked(
                VacancyModel vacancy, @VacancyCardViewAdapter.VacancyPopupMenuType int type);

        void clear();

        void updateVacanciesTimeStatus();

        Pair<Boolean,Set<String>> getFilterItems();

        void filterUpdated(Pair<Boolean, Set<String>> data);
    }
}
