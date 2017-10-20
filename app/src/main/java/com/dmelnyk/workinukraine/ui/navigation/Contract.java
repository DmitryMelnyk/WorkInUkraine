package com.dmelnyk.workinukraine.ui.navigation;

/**
 * Created by d264 on 6/14/17.
 */

public class Contract {

    public interface INavigationView {
//        void restoreSavedState(String time);
    }

    public interface INavigationPresenter {
        void bindView(INavigationView view);
        void unbindView();
    }
}
