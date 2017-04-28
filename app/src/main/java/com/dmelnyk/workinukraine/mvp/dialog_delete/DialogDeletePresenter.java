package com.dmelnyk.workinukraine.mvp.dialog_delete;

import android.content.Context;
import android.util.Log;

import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.ActivityType;

import javax.inject.Inject;

import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.FAVORITE;
import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.NEW;
import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.RECENT;

/**
 * Created by dmitry on 03.04.17.
 */

public class DialogDeletePresenter implements Contract.Presenter {
    private static final String TAG = "GT.DialogDeletePres";

    @Inject
    JobPool jobPool;

    DialogDelete view;
    int typeActivity;

    public DialogDeletePresenter(Context context, @ActivityType int typeActivity) {
        Log.d(TAG, "creating constructor: DialogDeletePresenter(Context context)");
        this.typeActivity = typeActivity;
        // inject database dependency
        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onButtonClicked(ButtonType button) {
        switch (button) {
            case CANCEL:
                view.dialogDismiss();
                break;
            case OK:
                clearTable();
                view.dialogDismiss();
                view.getView().postDelayed(() -> view.closeActivity(), 300);
                break;
        }
    }

    @Override
    public void onTakeView(DialogDelete dialog) {
        view = dialog;
    }

    private void clearTable() {
        Log.d(TAG, "Clearing FAVORITE table");
        String table = "";
        switch (typeActivity) {
            case RECENT:
                table = JobDbSchema.JobTable.RECENT;
                break;
            case FAVORITE:
                table = JobDbSchema.JobTable.FAVORITE;
                break;
            case NEW:
                table = JobDbSchema.JobTable.NEW;
                break;
        }
        jobPool.clearTable(table);
    }
}
