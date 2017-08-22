package com.dmelnyk.workinukraine.ui.dialogs.request;

import java.util.ArrayList;

/**
 * Created by dmitry on 03.04.17.
 */

public class Contract {

    interface View {
        void showErrorMessage();

        void configSpinner();

        void dialogDismiss();
    }

    interface Presenter {
        void onButtonClicked(String request, String textRequest);

        void bindView(DialogRequest dialog);

        void unbindView();
    }

    // Model - is one class for all: JobPool
}
