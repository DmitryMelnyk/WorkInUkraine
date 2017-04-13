package com.dmelnyk.workinukraine.mvp.dialog_delete;

/**
 * Created by dmitry on 03.04.17.
 */

public class Contract {

    interface View {
        void dialogDismiss();

        void closeActivity();
    }

    interface Presenter {
        void onButtonClicked(ButtonType button);

        void onTakeView(DialogDelete dialog);
    }

    // Model - is one class for all: JobPool
}
