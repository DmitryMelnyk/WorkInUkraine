package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by d264 on 6/14/17.
 */

public class Contract {

    public interface ISearchView {
        void updateData(ArrayList<RequestModel> data);

        void updateNewVacanciesCount(int newVacanciesCount);

        void updateVacanciesCount(int allVacanciesCount);

        void showErrorMessage(String message);

        void updateLastSearchTime(String updated);
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);

        void editRequest(String previousRequest, String newRequest);

        void unbindView();

        void addRequest(String request);

        void removeRequest(String request);

        void clearAllRequest();

        void updateData();
    }
}
