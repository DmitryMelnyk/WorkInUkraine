package com.dmelnyk.workinukraine.mvp.activity_tabs;

import android.os.Bundle;

/**
 * Created by dmitry on 05.04.17.
 */

public class Contract {

    interface View {
        void onConfigSmartTabs(Bundle args);

        void onShowNetworkErrorMessage();
    }

    interface Presenter {
        void onTakeView(TabsActivity activity);

        void onButtonClicked();

        void onNavigationItemSelected(int id);

        void onBackPressed();
    }

    // Model - is one class for all: JobPool
}
