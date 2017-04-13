package com.dmelnyk.workinukraine.mvp.activity_search;

/**
 * Created by dmitry on 07.04.17.
 */

public class Contract {

    interface View {

    }

    interface Presenter {
        void onTakeView(SearchActivity activity);


        void onBackPressed();

        void onSearchButtonClicked();
    }
}
