package com.dmelnyk.workinukraine.mvp.activity_splash;

/**
 * Created by dmitry on 06.04.17.
 */

public class Contract {

    interface View {
        // NOP
    }

    interface Presenter {
        void onTakeView(SplashActivity activity);

        void onSplashClosed();
    }

    // Model - is one class for all: JobPool
}
