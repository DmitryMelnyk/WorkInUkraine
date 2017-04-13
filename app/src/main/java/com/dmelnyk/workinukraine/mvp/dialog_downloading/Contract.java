package com.dmelnyk.workinukraine.mvp.dialog_downloading;

/**
 * Created by dmitry on 03.04.17.
 */

public class Contract {

    interface View {
        void dialogDismiss();

        void updateLoader(int loaderCode, int size);

        void hideSpinner();

        void resetPresenter();
    }

    interface Presenter {
        void onButtonClicked();

        void onTakeView(DialogDownloading downloading);
    }

    // Model - is one class for all: JobPool
}
