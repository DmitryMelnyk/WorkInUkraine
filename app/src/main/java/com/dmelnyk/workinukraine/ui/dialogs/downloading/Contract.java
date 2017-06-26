package com.dmelnyk.workinukraine.ui.dialogs.downloading;

import java.util.ArrayList;

/**
 * Created by dmitry on 03.04.17.
 */

public class Contract {

    interface View {

        void dialogDismiss();
    }

    interface Presenter {
        void onButtonClicked(String request, String textRequest);

        void onTakeView(DialogDownloading dialog);
    }

    // Model - is one class for all: JobPool
}
