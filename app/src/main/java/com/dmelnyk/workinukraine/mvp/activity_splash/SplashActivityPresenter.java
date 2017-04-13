package com.dmelnyk.workinukraine.mvp.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dmelnyk.workinukraine.mvp.activity_search.SearchActivity;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivity;

import javax.inject.Inject;

/**
 * Created by dmitry on 06.04.17.
 */

public class SplashActivityPresenter implements Contract.Presenter {

    private static final String TAG = "GT.SplashActivityPresenter";

    private Context context;
    private SplashActivity view;
    private Bundle data;

    @Inject
    JobPool jobPool;

    public SplashActivityPresenter(Context context) {
        this.context = context;
        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTakeView(SplashActivity activity) {
        view = activity;
    }

    @Override
    public void onSplashClosed() {
        Intent intent;
        // run SearchActivity if database is empty
        if (!isDbEmpty()) {
            intent = new Intent(context, TabsActivity.class);
        } else {
            intent = new Intent(context, SearchActivity.class);
        }

        context.startActivity(intent);
    }

    private boolean isDbEmpty() {
        data = jobPool.getAllJobs();
        return data.size() == 0;
    }
}
