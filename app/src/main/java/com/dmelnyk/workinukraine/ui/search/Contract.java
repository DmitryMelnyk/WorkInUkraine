package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;
import com.dmelnyk.workinukraine.data.RequestModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d264 on 6/14/17.
 */

public class Contract {

    public interface ISearchView {
        void updateData(ArrayList<RequestModel> data);

        void updateNewVacanciesCount(int newVacanciesCount);

        void updateVacanciesCount(int allVacanciesCount);

        void showErrorMessage(String message);
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();

        void addNewRequest(String request);

        void removeRequest(String mItemClicked);

        void clearAllRequest();

        void getFreshRequests();
    }
}
