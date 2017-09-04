package com.dmelnyk.workinukraine.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;

import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyContainer;
import com.dmelnyk.workinukraine.db.di.DaggerDbComponent;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.data.search_service.ISearchServiceRepository;
import com.dmelnyk.workinukraine.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.parsing.ParserWorkUa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public static final String KEY_TOTAL_VACANCIES_COUNT = "vacancies count";

    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;

    private Map<String, Integer> vacancyInfos;

    public SearchVacanciesService() {
        super(SearchVacanciesService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("\nSearching vacancies started!");
        vacancyInfos = new HashMap<>();

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

        ExecutorService pool = Executors.newCachedThreadPool();
        // Creating parallel search tasks for each search request
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
                List<VacancyContainer> vacancies = (List<VacancyContainer>) future.get();
                addVacancyInfo(vacancies);
                Timber.d(" Found vacancies: " + vacancies.size());
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        sendBroadcastMessage(ACTION_FINISHED, null /* request no need */, totalVacanciesCount);
        long endTime = System.currentTimeMillis();
        Timber.d("\nSearch completed at %d seconds", (endTime - startTime) / 1000);
    }

    private void addVacancyInfo(List<VacancyContainer> vacancies) {
        if (!vacancies.isEmpty()) {
            String request = vacancies.get(0).getVacancy().request();
            int count = vacancies.size();
            if (vacancyInfos.get(request) != null) {
                vacancyInfos.put(request, vacancyInfos.get(request) + count);
            } else {
                vacancyInfos.put(request, count);
            }
        }
    }

    private void sendBroadcastMessage(String action, String request, int vacanciesCount) {
        Timber.d(" Sending broadcast: " + action);

        Intent broadcast;
        switch (action) {
            case ACTION_FINISHED:
                broadcast = new Intent(ACTION_FINISHED);
                broadcast.putExtra(KEY_TOTAL_VACANCIES_COUNT, vacanciesCount);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
                break;

            case ACTION_DOWNLOADING_IN_PROGRESS:
                broadcast = new Intent(ACTION_DOWNLOADING_IN_PROGRESS);
                broadcast.putExtra(KEY_REQUEST, request);
                broadcast.putExtra(KEY_TOTAL_VACANCIES_COUNT, vacanciesCount);
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
                break;
        }
    }

    private class CallableSearchTask implements Callable<List<VacancyContainer>> {
        private final int code;
        private final String request;

        public CallableSearchTask(int code, String request) {
            this.code = code;
            this.request = request;
        }

        @Override
        public List<VacancyContainer> call() throws Exception {
            Timber.d(" Starting task " + code);
            List<VacancyContainer> list = new ArrayList<>();

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

//            repository.saveVacancies(Tables.SearchSites.TYPE_SITES[code], list);
            saveData(request, list);
            sendBroadcastMessage(ACTION_DOWNLOADING_IN_PROGRESS, request, list.size());

            return list;
        }
    }

    private volatile int totalVacanciesCount = 0;
    private volatile Map<String, Integer> count = new HashMap<>();
    private volatile Map<String, List<VacancyContainer>> data = new HashMap<>();

    private void saveData(String request, List<VacancyContainer> list) {
        totalVacanciesCount += list.size();

        if (count.containsKey(request)) {
            count.put(request, count.get(request) + 1);
        } else {
            count.put(request, 1);
        }

        if (!data.containsKey(request)) {
            List<VacancyContainer> vacancyList = new ArrayList<>();
            data.put(request, vacancyList);
        }

        data.get(request).addAll(list);

        if (count.get(request) == 5) {
            repository.saveVacancies(data.get(request));
        }
    }
}
