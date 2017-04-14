package com.dmelnyk.workinukraine.mvp.activity_webview;

import android.content.Context;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.helpers.Job;

import javax.inject.Inject;

/**
 * Created by dmitry on 14.04.17.
 */
public class WebActivityPresenter implements Contract.Presenter {

    private Context context;
    private WebViewActivity view;
    private Job job;

    private final int menuDefault = R.menu.webview_menu_default;
    private final int menuAddedToFavorite = R.menu.webview_menu_added_to_favorite;

    @Inject
    JobPool jobPool;

    public WebActivityPresenter(Context context, Job job) {
        this.context = context;
        this.job = job;

        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onMenuItemSelected() {
        int menu;
        String message;
        if (!jobPool.containsJob(JobDbSchema.JobTable.FAVORITE, job)) {
            jobPool.addJob(JobDbSchema.JobTable.FAVORITE, job);
            menu = menuAddedToFavorite;
            // vacancy added to favorite-list
            message = context.getResources().getString(R.string.card_view_adapter_job_added_to_favorite);
        } else {
            // remove vacancy from favorite-list
            jobPool.removeJobFromFavorite(job);
            menu = menuDefault;
            message = context.getResources().getString(R.string.card_view_adapter_job_removed_from_favorite);
        }
        view.onShowToast(message);
        view.onChangeMenu(menu);
    }

    @Override
    public void onTakeView(WebViewActivity activity) {
        view = activity;
        if (view != null) {
            initializeMenu();
        }
    }

    private void initializeMenu() {
        int startMenu;
        if (jobPool.containsJob(JobDbSchema.JobTable.FAVORITE, job)) {
            startMenu = menuAddedToFavorite;
        } else {
            startMenu = menuDefault;
        }
        view.onChangeMenu(startMenu);
    }
}
