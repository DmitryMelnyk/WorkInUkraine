package com.dmelnyk.workinukraine.ui.navigation.repository;

import android.content.Context;

/**
 * Created by d264 on 6/23/17.
 */

public class NavigationRepository extends INavigationRepository {
    Context mContext;

    public NavigationRepository(Context context) {
        this.mContext = context;
    }
}
