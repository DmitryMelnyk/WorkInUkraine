package com.dmelnyk.workinukraine.ui.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by dmitry on 06.04.17.
 */

public class SplashActivityPresenter implements Contract.Presenter {

    private static final String TAG = "GT.SplashActivityPresenter";

    private Context context;
    private SplashActivity view;
    private Bundle data;

    public SplashActivityPresenter(Context context) {
        this.context = context;
    }

    @Override
    public void onTakeView(SplashActivity activity) {
        view = activity;
    }

    // TODO: update
    @Override
    public void onSplashClosed() {
        Intent intent;
        // run SearchActivity if database is empty
//        if (!isDbEmpty()) {
//            intent = new Intent(context, TabsActivity.class);
//        } else {
//            intent = new Intent(context, SearchActivity.class);
//        }

//        context.startActivity(intent);
    }
}
