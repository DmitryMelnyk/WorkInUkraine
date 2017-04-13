package com.dmelnyk.workinukraine.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.dmelnyk.workinukraine.db.JobDbSchema;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.di.component.DbComponent;
import com.dmelnyk.workinukraine.helpers.Job;
import com.dmelnyk.workinukraine.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.parsing.ParserWorkUa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

/**
 * Created by dmitry on 09.03.17.
 */

public class GetDataIntentService extends IntentService {

    // SEARCH - for DialogDownloading, SERVICE - for BroadcastReceiver
    public enum Mode {
        SEARCH, SERVICE;
    }

    public static final String TAG = "GT.GetDataIs";
    public static final String KEY_CITY = "city";
    public static final String KEY_REQUEST = "request";
    public static final String KEY_MODE = "mode";

    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;
    public static final int FINISH_CODE = -1;
    static Handler mainHandler;
    String city;
    String search;
    Mode mode;

    @Inject
    JobPool jobPool;

    private void injectDependency() {
        DaggerDbComponent.builder()
                .applicationComponent(MyApplication.get(this).getAppComponent())
                .build()
                .inject(this);
    }

    public static void giveHandler(Handler handler) {
        mainHandler = handler;
    }

    public GetDataIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "start OnHandleIntent()");
        injectDependency();

        city = intent.getStringExtra(KEY_CITY);
        search = intent.getStringExtra(KEY_REQUEST);

        mode = (Mode) intent.getSerializableExtra(KEY_MODE);
        if (mode.equals(Mode.SEARCH)) {
            jobPool.clearDb();
            jobPool.clearTable(JobDbSchema.JobTable.RECENT);
        }

        Bundle jobs = new Bundle();
        ExecutorService threads = Executors.newCachedThreadPool();

        List<Future<ArrayList<Job>>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            tasks.add(
                    threads.submit(new Callable<ArrayList<Job>>() {
                        @Override
                        public ArrayList<Job> call() throws Exception {
                            ArrayList<Job> list = new ArrayList<>();
                            switch (finalI) {
                                case HEADHUNTERSUA:
                                    list = new ParserHeadHunters(getApplicationContext()).getJobs(city, search);
                                    break;
                                case JOBSUA:
                                    list = new ParserJobsUa(getApplicationContext()).getJobs(city, search);
                                    break;
                                case RABOTAUA:
                                    list = new ParserRabotaUa(getApplicationContext()).getJobs(city, search);
                                    break;
                                case WORKNEWINFO:
                                    list = new ParserWorkNewInfo(getApplicationContext()).getJobs(city, search);
                                    break;
                                case WORKUA:
                                    list = new ParserWorkUa(getApplicationContext()).getJobs(city, search);
                                    break;
                            }
                            jobs.putParcelableArrayList(JobDbSchema.JobTable.NAMES[finalI], list);

                            Message message;
                            if (mode.equals(Mode.SEARCH)) { // sending data for updating DialogDownloading
                                message = mainHandler.obtainMessage(finalI, list.size(), -1); // -1 is needed for creating Message(int, int, int)
                                mainHandler.sendMessage(message);
                                // write list to DB
                                jobPool.addJobs(list, JobDbSchema.JobTable.NAMES[finalI]);
                            }
                            return list;
                        }
                    }));
        }

        for (Future<ArrayList<Job>> list : tasks) {
            try {
                Log.d(TAG, "Result TaskExecutor: " + list.get().size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // after competing tasks
        Log.d(TAG, "Sending message with jobs list: " + jobs.size());
        Message message = mainHandler.obtainMessage(FINISH_CODE, jobs);
        mainHandler.sendMessage(message);
        jobPool.closeDb();
    }
}
