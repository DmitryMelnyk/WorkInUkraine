package com.dmelnyk.workinukraine.mvp.activity_settings;

import android.content.SharedPreferences;

/**
 * Created by dmitry on 07.04.17.
 */

public class Contract {

    interface View {

    }

    interface Presenter {
        void onTakeView(FragmentSettings fragment);

        void onChangeSettings(SharedPreferences sharedPreferences, String key);
    }
}
