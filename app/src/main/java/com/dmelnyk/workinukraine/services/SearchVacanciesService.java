package com.dmelnyk.workinukraine.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dmelnyk.workinukraine.data.RequestModel;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.db.di.DaggerDbComponent;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.model.search_service.ISearchServiceRepository;
import com.dmelnyk.workinukraine.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.parsing.ParserWorkUa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by dmitry on 09.03.17.
 */

public class SearchVacanciesService extends IntentService {

    // MODE_SEARCH - for DialogDownloading, SERVICE - for BroadcastReceiver
    public static final int MODE_SEARCH = -1;
    public static final int SERVICE = -2;
    public static final String ACTION_FINISHED = "downloading finished";
    public static final String ACTION_DOWNLOADING_IN_PROGRESS = "downloading in process";

    @IntDef({MODE_SEARCH, SERVICE })
    @Retention(RetentionPolicy.CLASS)
    public @interface Mode {}

    @Inject
    ISearchServiceRepository repository;

    public static final String KEY_MODE = "mode";
    public static final String KEY_REQUESTS = "requests";
    public static final String KEY_REQUEST = "downloading request";
    public static final String KEY_VACANCIES_COUNT = "vacancies count";

    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;

    public SearchVacanciesService() {
        super(SearchVacanciesService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);

        Timber.d("\nSearching vacancies started!");
        long startTime = System.currentTimeMillis();

        DaggerDbComponent
                .builder()
                .dbModule(new DbModule(getApplicationContext()))
                .build()
                .inject(this);

        ArrayList<RequestModel> requests = intent.getParcelableArrayListExtra(KEY_REQUESTS);
        int mode = intent.getIntExtra(KEY_MODE, -2);

        if (mode == MODE_SEARCH) {
        }

        repository.clearNewTable();

        ExecutorService pool = Executors.newCachedThreadPool();
        List<Future> futures = new ArrayList<>();
        for (RequestModel request : requests) {
            CallableSearchTask[] callables = new CallableSearchTask[5];

            for (int i = 0; i < 5; ++i) {
                callables[i] = new CallableSearchTask(i, request.request());
                futures.add(pool.submit(callables[i]));
            }
        }

        for (Future future : futures) {
            try {
                List<VacancyModel> vacancies = (List<VacancyModel>) future.get();
                Timber.d(" Found vacancies: " + vacancies.size());
            } catch (Exception e) {
                Timber.e(e);
            }
        }

//        repository.closeDb();
        sendBroadcastMessage(ACTION_FINISHED, null, -1);
        long endTime = System.currentTimeMillis();
        Timber.d("\nSearch completed at %d seconds", (endTime - startTime) / 1000);
    }

    private void sendBroadcastMessage(String action, String request, int vacanciesCount) {
        Timber.d(" Sending broadcast: " + action);

        Intent broadcast;
        switch (action) {
            case ACTION_FINISHED:
                broadcast = new Intent(ACTION_FINISHED);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
                break;

            case ACTION_DOWNLOADING_IN_PROGRESS:
                broadcast = new Intent(ACTION_DOWNLOADING_IN_PROGRESS);
                broadcast.putExtra(KEY_REQUEST, request);
                broadcast.putExtra(KEY_VACANCIES_COUNT, vacanciesCount);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
                break;
        }
    }

    private class CallableSearchTask implements Callable<List<VacancyModel>> {
        private final int code;
        private final String request;

        public CallableSearchTask(int code, String request) {
            this.code = code;
            this.request = request;
        }

        @Override
        public List<VacancyModel> call() throws Exception {
            Timber.d(" Starting task " + code);
            List<VacancyModel> list = new ArrayList<>();

            switch (code) {
                case HEADHUNTERSUA:
                    list = new ParserHeadHunters(getApplicationContext()).getJobs(request);
                    break;
                case JOBSUA:
                    list = new ParserJobsUa(getApplicationContext()).getJobs(request);
                    break;
                case RABOTAUA:
                    list = new ParserRabotaUa(getApplicationContext()).getJobs(request);
                    break;
                case WORKNEWINFO:
                    list = new ParserWorkNewInfo(getApplicationContext()).getJobs(request);
                    break;
                case WORKUA:
                    list = new ParserWorkUa(getApplicationContext()).getJobs(request);
                    break;
            }

            repository.saveVacancies(Tables.SearchSites.SITES[code], list);
            sendBroadcastMessage(ACTION_DOWNLOADING_IN_PROGRESS, request, list.size());

            return list;
        }
    }
}
