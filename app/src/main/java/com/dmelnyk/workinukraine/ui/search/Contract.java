package com.dmelnyk.workinukraine.ui.search;

import com.dmelnyk.workinukraine.business.search.model.SearchRequestModel;

/**
 * Created by d264 on 6/14/17.
 */

public class Contract {

    public interface ISearchView {
        void restoreSavedState(String time);

        void updateData(SearchRequestModel[] data);
    }

    public interface ISearchPresenter {
        void bindView(ISearchView view);
        void unbindView();
    }
}
