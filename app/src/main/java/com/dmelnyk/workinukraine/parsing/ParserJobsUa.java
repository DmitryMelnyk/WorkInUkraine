package com.dmelnyk.workinukraine.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.di.component.DaggerUtilComponent;
import com.dmelnyk.workinukraine.helpers.CityUtils;
import com.dmelnyk.workinukraine.helpers.Job;
import com.dmelnyk.workinukraine.helpers.NetUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserJobsUa {

    private static final String TAG = "TAG.ParserJobsUa";
    @Inject
    NetUtils netUtils;

    @Inject
    CityUtils cities;

    public ParserJobsUa(Context context) {
        DaggerUtilComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    /**
     * Parse page with given jobRequest parameters.
     * @param city - city field.
     * @param jobRequest - search parameter
     * @return list of Job elements or empty ArrayList
     */
    @NonNull
    public ArrayList<Job> getJobs(String city, String jobRequest) {
        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = cities.getCityId(CityUtils.SITE.JOBSUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.jobs.ua/vacancy/" +
                cityId + "/rabota-" + correctedRequest;

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return jobs;
        }

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("div[class=\"b-vacancy__top\"]");
        for (Element link : links) {
            String title = link.select("a").attr("title");
            String url = link.select("a").attr("href");
            String date = link.select("span").text();
            Job job = new Job(title, date, url);
            jobs.add(job);
        }

        Log.d(TAG, "found " + jobs.size() + " vacancies");
        return jobs;
    }
}
