package com.dmelnyk.workinukraine.mvp.activity_tabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.helpers.NetUtils;
import com.dmelnyk.workinukraine.helpers.Tags;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity;
import com.dmelnyk.workinukraine.mvp.activity_search.SearchActivity;
import com.dmelnyk.workinukraine.mvp.activity_settings.SettingsActivity;
import com.dmelnyk.workinukraine.services.GetDataIntentService;

import javax.inject.Inject;

import static com.dmelnyk.workinukraine.services.GetDataIntentService.FINISH_CODE;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_CITY;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_MODE;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_REQUEST;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.giveHandler;

/**
 * Created by dmitry on 05.04.17.
 */

public class TabsActivityPresenter implements Contract.Presenter {

    public static final int HANDLER_DOWNLOADING_DIALOG_RESULT = 0;
    public static final int HANDLER_SEARCH_DIALOG_RESULT = 1;
    private static final String TAG = "GT.TabsActivityPr";

    private TabsActivity view;
    private Context context;
    @Inject
    JobPool jobPool;

    @Inject
    SharedPreferences sharedPreferences;

    public TabsActivityPresenter(Context context) {
        this.context = context;

        DaggerDbComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTakeView(TabsActivity activity) {
        view = activity;
        if (view != null) {
            updateUi();
        }
    }

    @Override
    public void onButtonClicked() {

        if (!NetUtils.isNetworkReachable(context)) {
            view.onShowNetworkErrorMessage();
        } else {
            runSearchActivity();
        }
    }

    @Override
    public void onNavigationItemSelected(int id) {
        switch (id) {
            case R.id.nav_new_request:
                if (!NetUtils.isNetworkReachable(context)) {
                    view.onShowNetworkErrorMessage();
                } else {
                    runSearchActivity();
                }
                break;
            case R.id.nav_update:
                if (!NetUtils.isNetworkReachable(context)) {
                    view.onShowNetworkErrorMessage();
                } else {
                    runUpdateData();
                }
                break;
            case R.id.nav_favorite:
                runBaseActivity(BaseActivity.FAVORITE);
                break;
            case R.id.nav_recent:
                runBaseActivity(BaseActivity.RECENT);
                break;
            case R.id.nav_settings:
                runSettingsActivity();
                break;
//            case R.id.nav_about:
//                // TODO
//                break;
            case R.id.nav_exit:
                exitApp();
                break;
        }
    }

    private void runSettingsActivity() {
        Intent settings = new Intent(context, SettingsActivity.class);
        context.startActivity(settings);
    }

    private void exitApp() {
        view.finish();
    }

    private void runBaseActivity(@BaseActivity.ActivityType int type) {
        Intent intentFavorite = new Intent(context, BaseActivity.class);
        intentFavorite.putExtra(BaseActivity.ACTIVITY_TYPE, type);
        context.startActivity(intentFavorite);
    }

    private void updateUi() {
        // Retrieve data from Db
        Bundle allJobs = jobPool.getAllJobs();
        view.onConfigSmartTabs(allJobs);
    }

    private void runSearchActivity() {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
        view.finish();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Message from DialogRequest
                case FINISH_CODE:
                    Intent i = new Intent(context, TabsActivity.class);
                    view.finish();
                    context.startActivity(i);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void runUpdateData() {
        String requestKeyWords = sharedPreferences.getString(Tags.SH_PREF_REQUEST_KEY_WORDS, "");
        String requestCity = sharedPreferences.getString(Tags.SH_PREF_REQUEST_CITY, "");

        // show downloading spinner
        view.spinner.show();
        update(context, handler, requestKeyWords, requestCity);
    }

    private void update(Context context, Handler handler, String requestText, String requestCity) {
        giveHandler(handler);
        Intent intent = new Intent(context, GetDataIntentService.class);
        intent.putExtra(KEY_CITY, requestCity);
        intent.putExtra(KEY_REQUEST, requestText);
        intent.putExtra(KEY_MODE, GetDataIntentService.SEARCH);
        context.startService(intent);
    }


    @Override
    public void onBackPressed() {
        if (view.drawer.isDrawerOpen(GravityCompat.START)) {
            view.drawer.closeDrawer(GravityCompat.START);
        }
        else {
            jobPool.closeDb();
            view.finish();
        }
    }
}
