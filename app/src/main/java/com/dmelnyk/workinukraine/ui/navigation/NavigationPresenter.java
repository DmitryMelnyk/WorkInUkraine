package com.dmelnyk.workinukraine.ui.navigation;

import com.dmelnyk.workinukraine.data.navigation.INavigationRepository;

/**
 * Created by d264 on 6/14/17.
 */

public class NavigationPresenter implements Contract.INavigationPresenter {

    private INavigationRepository repository;
    private Contract.INavigationView view;

    public NavigationPresenter(INavigationRepository repository) {
        this.repository = repository;
    }

    @Override
    public void bindView(Contract.INavigationView view) {
        this.view = view;

    }

    @Override
    public void unbindView() {
        view = null;
    }
}
