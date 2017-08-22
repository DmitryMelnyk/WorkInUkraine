package com.dmelnyk.workinukraine.ui.dialogs.request;

import android.content.Context;
import android.util.Log;

import timber.log.Timber;

/**
 * Created by dmitry on 03.04.17.
 */

public class DialogRequestPresenter implements Contract.Presenter {
    private static final String TAG = "GT.DialogDeletePres";
    DialogRequest view;

    public DialogRequestPresenter() {
        Timber.d("Creating DialogRequestPresenter");
    }

    @Override
    public void onButtonClicked(String textRequest, String cityRequest) {
        boolean correct = checkRequestSize(textRequest);

        if (correct) {
            view.dialogDismiss();
            unbindView();
//            saveRequestsToSharedPrefs(textRequest, cityRequest);
        } else {
            view.showErrorMessage();
        }
    }

//    private void saveRequestsToSharedPrefs(String requestKeyWords, String requestCity) {
//        sharedPreferences.edit()
//                .putString(Tags.SH_PREF_REQUEST_KEY_WORDS, requestKeyWords)
//                .putString(Tags.SH_PREF_REQUEST_CITY, requestCity)
//                .apply();
//    }

    // Minimal request length must be 3 symbols
    private boolean checkRequestSize(String request) {
        return request.length() >= 3;
    }

    @Override
    public void bindView(DialogRequest dialog) {
        view = dialog;
    }

    @Override
    public void unbindView() {
        view = null;
    }
}
