package com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new;

import com.dmelnyk.workinukraine.helpers.Job;

import java.util.ArrayList;

/**
 * Created by dmitry on 04.04.17.
 */

public class Contract {

    interface View {
        void onShowDialogDelete();

        void onSetEmptyBaseView();

        void onSetBaseView(ArrayList<Job> jobs);
    }

    interface Presenter {
        void onTakeView(BaseActivity activity, int typeActivity);

        void onButtonClicked();
    }
}
