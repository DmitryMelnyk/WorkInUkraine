package com.dmelnyk.workinukraine.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.di.MyApplication;
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

public class ParserWorkUa {

    private static final String TAG = "TAG.ParserWorkUa";
    @Inject
    NetUtils netUtils;

    @Inject CityUtils cities;

    public ParserWorkUa(Context context) {
        DaggerUtilComponent.builder()
                .applicationComponent(MyApplication.get(context).getAppComponent())
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

        String cityId = cities.getCityId(CityUtils.SITE.WORKUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.work.ua/jobs-" +
                cityId + "-" + correctedRequest;

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return jobs;
        }

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("h2").select("a");
        for (Element link : links) {
            String title = link.text();
            String url = "https://www.work.ua" + link.attr("href");
            String date = link.attr("title")
                    .substring(title.length() + ", вакансия от ".length());
            Job job = new Job(title, date, url);
            jobs.add(job);
        }
        Log.d(TAG, "found " + jobs.size() + " vacancies");

        return jobs;
    }
}
