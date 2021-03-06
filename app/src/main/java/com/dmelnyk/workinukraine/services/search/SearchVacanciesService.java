package com.dmelnyk.workinukraine.services.search;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.db.di.DaggerDbComponent;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.services.search.repository.ISearchServiceRepository;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.utils.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkUa;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by dmitry on 09.03.17.
 */

public class SearchVacanciesService extends Service {

    // MODE_SEARCH - for DialogDownloading, MODE_REPEATING_SEARCH - for BroadcastReceiver
    public static final int MODE_SEARCH = -1;
    public static final int MODE_REPEATING_SEARCH = -2;
    public static final String ACTION_FINISHED = "downloading finished";
    public static final String ACTION_FINISHED_REPEATING_SEARCH = "downloading_finished_repeating_mode";
    public static final String ACTION_DOWNLOADING_IN_PROGRESS = "downloading in process";

    public static boolean sIsDownloadingFinished = false;
    public static int sVacanciesCount = 0;

    private ExecutorService pool;
    private boolean mIsJobCanceled;

    @IntDef({MODE_SEARCH, MODE_REPEATING_SEARCH})
    @Retention(RetentionPolicy.CLASS)
    public @interface Mode {}

    @Inject ISearchServiceRepository repository;

    public static final String EXTRA_MODE = "mode";
    public static final String KEY_REQUEST = "downloading request";
    public static final String KEY_TOTAL_VACANCIES_COUNT = "vacancies count";

    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;

    private int mMode;

    // Binder given to clients
    private final IBinder mBinder = new SearchServiceBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getSimpleName(), "onCreate()");
        DaggerDbComponent.builder()
                .dbModule(new DbModule(getApplicationContext()))
                .build()
                .inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(getClass().getSimpleName(), "onStartCommand()");
        startSearchingProcess(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startSearchingProcess(Intent intent) {
        // default values
        sIsDownloadingFinished = false;
        sVacanciesCount = 0;

        if (intent == null) {
            mMode = MODE_REPEATING_SEARCH;
        } else {
            mMode = intent.getIntExtra(EXTRA_MODE, MODE_REPEATING_SEARCH);
        }

        // Gets request list and starts searching
        repository.getRequests()
                .subscribe(requests -> {
                    Log.e(getClass().getSimpleName(), "repository.getRequests()=" + requests);
                    startSearching(requests);
                });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void cancelDownloading() {
        Log.e(getClass().getSimpleName(), "stopService()");
        if (pool != null) {
            pool.shutdownNow();
            mIsJobCanceled = true;
            sIsDownloadingFinished = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(getClass().getSimpleName(), "onDestroy()");
    }

    private synchronized void startSearching(List<RequestModel> requests) {
        Log.d(getClass().getSimpleName(), "startSearching. Requests=" + requests);
        /*ExecutorService */pool = Executors.newCachedThreadPool();
        // Creating parallel search tasks for each search request
        long startTime = System.nanoTime();

        List<Future> futures = new ArrayList<>();
        for (RequestModel requestModel : requests) {
            SearchVacanciesTask[] callables = new SearchVacanciesTask[5];

            // Creates and starts 5 task for each request search
            for (int i = 0; i < 5; ++i) {
                callables[i] = new SearchVacanciesTask(i, requestModel.request());
                futures.add(pool.submit(callables[i]));
            }
        }

        // Waiting for result
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                Timber.e(e);
            }
        }

        sendBroadcastMessage(ACTION_FINISHED, null /* request no need */, totalVacanciesCount);
        long endTime = System.nanoTime();
        Timber.d("\nSearch completed at %d seconds", (endTime - startTime) / 1000000000);
        stopSelf();
    }

    private void sendBroadcastMessage(String action, String request, int vacanciesCount) {
        Timber.d(" Sending broadcast: " + action);

        // if job is canceled don't send any messages
        if (mIsJobCanceled) return;

        Intent broadcast = null;
        switch (action) {
            case ACTION_FINISHED:
                sIsDownloadingFinished = true;
                sVacanciesCount = vacanciesCount;
                switch (mMode) {
                    // Searching when the app is running
                    case MODE_SEARCH:
                        broadcast = new Intent(ACTION_FINISHED);
                        broadcast.putExtra(KEY_TOTAL_VACANCIES_COUNT, vacanciesCount);
                        break;

                    // Searching in repeated process when the app is shut down
                    case MODE_REPEATING_SEARCH:
                        broadcast = new Intent(ACTION_FINISHED_REPEATING_SEARCH);
                        // no need in known vacancies count;
                        break;
                }

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

    private class SearchVacanciesTask implements Callable<List<VacancyModel>> {
        private final int code;
        private final String request;

        public SearchVacanciesTask(int code, String request) {
            this.code = code;
            this.request = request;
        }

        @Override
        public List<VacancyModel> call() throws Exception {
            Timber.d(" Starting task " + code);
            List<VacancyModel> list = new ArrayList<>();

            String site = DbContract.SearchSites.SITES[code];
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

            // if job is canceled don't do anything
            if (!mIsJobCanceled) {
                // after each of 5 request save cache to 'count'.
                // Then save all cache in repository after getting all result from 5 sites
                saveDataToMap(request, list, site);
                int listSize = list != null
                        ? list.size()
                        : 0;
                sendBroadcastMessage(ACTION_DOWNLOADING_IN_PROGRESS, request, listSize);
            }

            return list;
        }
    }

    private volatile int totalVacanciesCount = 0;

    private volatile Map<String, Integer> count = new HashMap<>();
    private volatile Map<String, List<VacancyModel>> cache = new HashMap<>();
    private volatile Map<String, Set<String>> sitesMap = new HashMap<>();

    private void saveDataToMap(String request, List<VacancyModel> list, String site) throws Exception {
        if (list != null) {
            totalVacanciesCount += list.size();
        }

        // adds response site to map for clearing only this old vacancies
        if (list != null) {
            Set<String> sites = sitesMap.containsKey(request)
                    ? sitesMap.get(request)
                    : new HashSet<>();

            sites.add(site);
            sitesMap.put(request, sites);
        }

        if (count.containsKey(request)) {
            count.put(request, count.get(request) + 1);
        } else {
            count.put(request, 1);
        }

        if (!cache.containsKey(request)) {
            List<VacancyModel> vacancyList = new ArrayList<>();
            cache.put(request, vacancyList);
        }

        // saving vacancies to cache
        if (list != null) {
            cache.get(request).addAll(list);
        }

        // Saves vacancies to db after getting results from all 5 sites
        if (count.get(request) == 5) {
            repository.saveVacancies(cache.get(request), sitesMap.get(request));
        }
    }

    public class SearchServiceBinder extends Binder {
        public SearchVacanciesService getService() {
            return SearchVacanciesService.this;
        }

        public void startSearching(Intent intent) {
            startSearchingProcess(intent);
        }
    }
}
