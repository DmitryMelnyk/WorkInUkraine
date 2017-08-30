package com.dmelnyk.workinukraine.ui.vacancy_webview;

/**
 * Created by dmitry on 14.04.17.
 */

public class Contract {

    interface View {
        void onChangeMenu(int id);

        void onShowToast(String message);
    }

    interface Presenter {
        void onMenuItemSelected();

        void onTakeView(WebViewActivity activity);
    }

    // Model - is one class for all: JobPool
}
