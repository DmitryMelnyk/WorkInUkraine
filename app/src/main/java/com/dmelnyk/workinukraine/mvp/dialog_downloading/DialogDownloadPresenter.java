package com.dmelnyk.workinukraine.mvp.dialog_downloading;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.di.component.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.helpers.RepeatingSearch;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivityPresenter;
import com.dmelnyk.workinukraine.services.GetDataIntentService;

import javax.inject.Inject;

import static com.dmelnyk.workinukraine.services.GetDataIntentService.FINISH_CODE;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_CITY;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_MODE;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.KEY_REQUEST;
import static com.dmelnyk.workinukraine.services.GetDataIntentService.giveHandler;

/**
 * Created by dmitry on 03.04.17.
 */

public class DialogDownloadPresenter
    implements Contract.Presenter {

    private static final String TAG = "GT.DialogDownloadPres";
    public static final int REQUEST_CODE = -1; // for pending intent

    private Context context;
    private Handler uiHandler;
    private DialogDownloading view;
    private String requestText;
    private String requestCity;
    public static AlarmManager am;
    private PendingIntent pending;
    private boolean searchSuccessfullyCompleted = false;
    Bundle jobs = new Bundle();

    @Inject
    RepeatingSearch repeatingSearch;

    public DialogDownloadPresenter(Context context, Handler handler, String requestText, String requestCity) {
        Log.d(TAG, "creating constructor: DialogDownloadPresenter(Context context, Handler handler)");
        this.context = context;
        this.uiHandler = handler;
        this.requestText = requestText;
        this.requestCity = requestCity;

        injectDependency(context);
    }

    private void injectDependency(Context context) {
        DaggerRepeatingSearchComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    // handler for receiving data from sites to display loading process
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final int what = msg.what;
            final int listSize = msg.arg1;

            Log.d(TAG, "received new Message " + msg.arg1);

            if (what == FINISH_CODE) {
                searchSuccessfullyCompleted = true;
                jobs = (Bundle) msg.obj;
                view.hideSpinner();
            } else {
                view.updateLoader(what, listSize);
            }
        }
    };

    private void sendResultToMainActivity() {
        Message message = Message.obtain(uiHandler,
                TabsActivityPresenter.HANDLER_DOWNLOADING_DIALOG_RESULT, jobs);
        uiHandler.sendMessage(message);
    }

    @Override
    public void onButtonClicked() {
        if (searchSuccessfullyCompleted) {
            sendResultToMainActivity();
        }

        // create repeating
        repeatingSearch.createRepeating();
        view.dialogDismiss();
        view.resetPresenter();
        view = null;
    }

    @Override
    public void onTakeView(DialogDownloading dialog) {
        view = dialog;
        if (view != null) {
            runDownloadingService(requestText, requestCity);
        }
    }

    // Create request to fetch data from job search cites
    private void runDownloadingService(String requestText, String requestCity) {
        giveHandler(handler);
        Intent intent = new Intent(context, GetDataIntentService.class);
        intent.putExtra(KEY_CITY, requestCity);
        intent.putExtra(KEY_REQUEST, requestText);
        intent.putExtra(KEY_MODE, GetDataIntentService.SEARCH);
        context.startService(intent);
    }
}
