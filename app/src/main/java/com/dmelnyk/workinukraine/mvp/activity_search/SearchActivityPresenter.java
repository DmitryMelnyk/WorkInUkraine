package com.dmelnyk.workinukraine.mvp.activity_search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivity;
import com.dmelnyk.workinukraine.mvp.dialog_downloading.DialogDownloading;
import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequest;

import javax.inject.Inject;


/**
 * Created by dmitry on 07.04.17.
 */

public class SearchActivityPresenter implements Contract.Presenter {

    private static final String DIALOG_REQUEST = "request_dialog";
    public static final int HANDLER_DOWNLOADING_DIALOG_RESULT = 0;
    public static final int HANDLER_SEARCH_DIALOG_RESULT = 1;

    Context context;

    @Inject
    JobPool jobPool;
    private SearchActivity view;

    public SearchActivityPresenter(Context context) {
        this.context = context;

        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    @Override
    public void onTakeView(SearchActivity activity) {
        view = activity;
    }


    @Override
    public void onSearchButtonClicked() {
        Fragment prev = view.getSupportFragmentManager().findFragmentByTag(DIALOG_REQUEST);
        if (prev != null){
            view.getSupportFragmentManager()
                    .beginTransaction().remove(prev);
        }

        runDialogRequest();
    }

    @Override
    public void onBackPressed() {
        // start TabsActivity if database have data
        if (jobPool.getAllJobs().size() > 0) {
            runTabsActivity();
        }
        view.finish();
    }

    private void runDialogRequest() {
        DialogRequest dialog =
                DialogRequest.getInstance(handler);
        dialog.show(view.getSupportFragmentManager(), DIALOG_REQUEST);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Message from DialogRequest
                case HANDLER_SEARCH_DIALOG_RESULT:
                    String[] requestData = (String[]) msg.obj;
                    runDialogDownloading(requestData);
                    break;
                case HANDLER_DOWNLOADING_DIALOG_RESULT:
                    runTabsActivity();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void runTabsActivity() {
        Intent intent = new Intent(context, TabsActivity.class);
        context.startActivity(intent);
        view.finish();
    }

    private void runDialogDownloading(String[] requestData) {
        String requestKeyWords = requestData[0];
        String requestCity = requestData[1];

        DialogDownloading.newInstance(handler,
                requestKeyWords, requestCity).
                show(view.getSupportFragmentManager(), "dialog_downloading");
    }
}
