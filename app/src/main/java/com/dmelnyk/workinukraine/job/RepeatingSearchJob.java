package com.dmelnyk.workinukraine.job;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.models.RequestModel;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.services.periodic_search.di.DaggerRepeatingSearchComponent;
import com.dmelnyk.workinukraine.services.periodic_search.di.RepeatingSearchModule;
import com.dmelnyk.workinukraine.services.periodic_search.repo.IRepeatingSearchRepository;
import com.dmelnyk.workinukraine.services.search.repository.ISearchServiceRepository;
import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;
import com.dmelnyk.workinukraine.utils.Tags;
import com.dmelnyk.workinukraine.utils.parsing.ParserHeadHunters;
import com.dmelnyk.workinukraine.utils.parsing.ParserJobsUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserRabotaUa;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkNewInfo;
import com.dmelnyk.workinukraine.utils.parsing.ParserWorkUa;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

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
 * Created by d264 on 10/16/17.
 */

public class RepeatingSearchJob extends Job {
    public static final String TAG = "RepeatingSearchJob_TAG";
    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;

    private Context mContext;

    public RepeatingSearchJob() {
        super();
    }

    @Inject
    IRepeatingSearchRepository settingsRepository;
    @Inject
    ISearchServiceRepository repository;

    public static void scheduleRepeatingSearch(long interval) {

        int jobId = new JobRequest.Builder(TAG)
                .setPeriodic(interval)
                .setRequiredNetworkType(JobRequest.NetworkType.NOT_ROAMING)
                .setUpdateCurrent(true)
                .build()
                .schedule();

        saveTaskId(jobId);
        Log.d(Tags.REPEATING_SEARCH, "scheduleRepeatingSearch(). jobId=" + jobId);
    }

    private static void saveTaskId(int jobId) {

    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(getClass().getSimpleName(), "RepeatingSearchJob.onRunJob()");
        mContext = getContext();

        DaggerRepeatingSearchComponent.builder()
                .dbModule(new DbModule(mContext))
                .repeatingSearchModule(new RepeatingSearchModule())
                .build()
                .inject(this);

        // Stops task if periodic search has been disabled in settings
        if (!settingsRepository.isPeriodicSearchEnable()) {
            JobManager.instance().cancelAllForTag(TAG);
            return Result.SUCCESS;
        }

        if (settingsRepository.getRequestCount() == 0) {
            // don't start repeating alarm if there is no request
            Log.d(getClass().getSimpleName(), "repeating alarm doesn't start because of empty request list");
            return Result.SUCCESS;
        }

        if (!settingsRepository.isSleepModeEnabled()) {
            // Starting searching service
            getRequestAndStartJob(mContext);
        } else {
            // check i is time to turn of service. In that case starts morning alarm
            // Starts repeating service if it is enable in settings and there is time
            // before sleep.
            if (settingsRepository.getCurrentTime().after(settingsRepository.getWakeTime())
                    && settingsRepository.getSleepFromTime().after(settingsRepository.getCurrentTime())){
                // Starting searching service
                getRequestAndStartJob(mContext);
            } else {
                Log.e(getClass().getSimpleName(), "Vacancy service doesn't started becouse it's time to sleep!");
            }
        }

        return Result.SUCCESS;
    }

    private void getRequestAndStartJob(Context context) {
        repository.getRequests()
                .subscribe(requests -> {
                    Log.d(getClass().getSimpleName(), "repository.getRequests()=" + requests);
                    startSearching(requests, context);
                });
    }

    private void startSearching(List<RequestModel> requests, Context context) {
        Log.d(getClass().getSimpleName(), "startSearching. Requests=" + requests);
        ExecutorService pool = Executors.newCachedThreadPool();
        // Creating parallel search tasks for each search request
        long startTime = System.nanoTime();

        List<Future> futures = new ArrayList<>();
        for (RequestModel requestModel : requests) {
            SearchVacanciesTask[] callables = new SearchVacanciesTask[5];

            // Creates and starts 5 task for each request search
            for (int i = 0; i < 5; ++i) {
                callables[i] = new SearchVacanciesTask(i, requestModel.request(), context);
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

        // After finishing search finds new vacancies
        checkNewVacancies();
        long endTime = System.nanoTime();
        Timber.d("\nSearch completed at %d seconds", (endTime - startTime) / 1000000000);
    }

    private class SearchVacanciesTask implements Callable<List<VacancyModel>> {
        private final int code;
        private final String request;
        private final Context context;

        public SearchVacanciesTask(int code, String request, Context context) {
            this.code = code;
            this.request = request;
            this.context = context;
        }

        @Override
        public List<VacancyModel> call() throws Exception {
            Timber.d(" Starting task " + code);
            List<VacancyModel> list = new ArrayList<>();

            String site = DbContract.SearchSites.SITES[code];
            // get list of vacancies from proper site
            switch (code) {
                case HEADHUNTERSUA:
                    list = new ParserHeadHunters(context).getJobs(request);
                    break;
                case JOBSUA:
                    list = new ParserJobsUa(context).getJobs(request);
                    break;
                case RABOTAUA:
                    list = new ParserRabotaUa(context).getJobs(request);
                    break;
                case WORKNEWINFO:
                    list = new ParserWorkNewInfo(context).getJobs(request);
                    break;
                case WORKUA:
                    list = new ParserWorkUa(context).getJobs(request);
                    break;
            }

            // after each of 5 request save cache to 'count'.
            // Then save all cache in repository after getting all result from 5 sites
            saveDataToMap(request, list, site);

            return list;
        }
    }

    private volatile Map<String, Integer> count = new HashMap<>();
    private volatile Map<String, List<VacancyModel>> cache = new HashMap<>();
    private volatile Map<String, Set<String>> sitesMap = new HashMap<>();

    private void saveDataToMap(String request, List<VacancyModel> list, String site) throws Exception {

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
        cache.get(request).addAll(list);

        // Saves vacancies to db after getting results from all 5 sites
        if (count.get(request) == 5) {
            repository.saveVacancies(cache.get(request), sitesMap.get(request));
        }
    }

    private void checkNewVacancies() {
        settingsRepository.getNewVacancies()
                .subscribe(newVacancies -> {
                    // Shows notification if you've found new vacancies
                    Log.d(getClass().getSimpleName(), "Found vacancies count=" + newVacancies.size());

                    int previousNewVacanciesCount = settingsRepository.getPreviousNewVacanciesCount();
                    Log.d(getClass().getSimpleName(), "Previous new vacancies count=" + previousNewVacanciesCount);
                    if (!newVacancies.isEmpty()
                            && newVacancies.size() != previousNewVacanciesCount) {
                        sendNotification(newVacancies.size());
                        settingsRepository.saveNewVacanciesCount(newVacancies.size());
                        Log.d(getClass().getSimpleName(), "new vacancies " + newVacancies.size());
                    } else {
                        Log.d(getClass().getSimpleName(), "no new vacancies!");
                    }

                    // Closing database
                    settingsRepository.close();
                }, throwable -> Timber.e("Error happened", throwable));

        // create new task after period
        scheduleRepeatingSearch(settingsRepository.getUpdateInterval());
    }

    private void sendNotification(int vacanciesFound) {
        final Bitmap largeExpandedAvatar = BitmapFactory.decodeResource(
                mContext.getResources(), R.mipmap.ic_launcher);

        boolean isVibroEnable = settingsRepository.isVibroEnable();
        boolean isSoundEnable = settingsRepository.isSoundEnable();

        // for Version >= 26
        initChannels(mContext);

        String msg = mContext.getString(R.string.msg_founded_new_vacancies) + " " + vacanciesFound;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(mContext, "default")
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_work_black_24dp)
                .setLargeIcon(largeExpandedAvatar)
                .setContentIntent(createPendingIntent(mContext))
                .setAutoCancel(true);

        if (isVibroEnable ) {
            notification.setVibrate(new long[]{1000, 1000});
        }

        if (isSoundEnable) {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.setSound(notificationSound);
        }

        NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
        nm.notify(NotificationManagerCompat.IMPORTANCE_DEFAULT, notification.build());
    }


    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }


    private PendingIntent createPendingIntent(Context context) {
        Intent intent = new Intent(context, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
