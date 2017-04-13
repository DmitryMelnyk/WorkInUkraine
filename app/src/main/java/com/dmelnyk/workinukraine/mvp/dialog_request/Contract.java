package com.dmelnyk.workinukraine.mvp.dialog_request;

import java.util.ArrayList;

/**
 * Created by dmitry on 03.04.17.
 */

public class Contract {

    interface View {
        void showErrorMessage();

        void configSpinner(ArrayList<String> items);

        void dialogDismiss();
    }

    interface Presenter {
        void onButtonClicked(String request, String textRequest);

        void onTakeView(DialogRequest dialog);
    }

    // Model - is one class for all: JobPool
}
