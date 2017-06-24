package com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new;

import android.content.Context;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.helpers.CardViewAdapter;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.ActivityType;
import com.dmelnyk.workinukraine.helpers.Job;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.FAVORITE;
import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.NEW;
import static com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.RECENT;

/**
 * Created by dmitry on 04.04.17.
 */

public class BaseActivityPresenter implements Contract.Presenter {

    private BaseActivity view;
    private Context context;
    private int adapterType;

    @Inject
    JobPool jobPool;

    public BaseActivityPresenter(Context context) {
        this.context = context;
        DaggerDbComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTakeView(BaseActivity activity, int typeActivity) {
        view = activity;
        updateUi(typeActivity);
    }

    @Override
    public void onButtonClicked() {
        view.onShowDialogDelete();
    }

    private void updateUi(@ActivityType int typeActivity) {
        if (view != null) {
            String tableName = getTable(typeActivity);
            String titleView = getTitle(typeActivity);
            setViewContent(tableName); // must be initialize before toolbar
            setViewTitle(titleView);

        }
    }

    private String getTable(@ActivityType int typeActivity) {
        String table = "";
        switch (typeActivity) {
            case FAVORITE:
                table = JobDbSchema.JobTable.FAVORITE;
                adapterType = CardViewAdapter.FAVORITE;
                break;
            case NEW:
                table = JobDbSchema.JobTable.NEW;
                adapterType = CardViewAdapter.TABVIEW;
                break;
            case RECENT:
                table = JobDbSchema.JobTable.RECENT;
                adapterType = CardViewAdapter.TABVIEW;
                break;
        }

        return table;
    }

    private void setViewTitle(String title) {
        view.configToolbar(title);
    }

    private String getTitle(@ActivityType int typeActivity) {
        String title = "";
        switch (typeActivity) {
            case FAVORITE:
                title = context.getString(R.string.base_activity_title_favorite);
                break;
            case NEW:
                title = context.getString(R.string.base_activity_title_new);
                break;
            case RECENT:
                title = context.getString(R.string.base_activity_title_recent);
                break;
        }

        return title;
    }

    private void setViewContent(String tableName) {
        ArrayList<Job> jobs = jobPool.getJobs(tableName);
        if (jobs.size() == 0) {
            view.onSetEmptyBaseView();
        } else {
            view.onSetBaseView(jobs);
            view.onConfigRecyclerView(jobs, adapterType);
        }
    }
}
