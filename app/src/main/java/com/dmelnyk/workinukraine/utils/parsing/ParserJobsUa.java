package com.dmelnyk.workinukraine.utils.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.CityUtils;
import com.dmelnyk.workinukraine.utils.NetUtils;
import com.dmelnyk.workinukraine.utils.di.DaggerUtilComponent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserJobsUa {

    private static final String TAG = "TAG.ParserJobsUa";

    NetUtils netUtils = NetUtils.getInstance();

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
     * @param request - Request in format "search request / city".
     * @return list of VacancyModule's or empty ArrayList
     * Result is null when server not response
     */
    @NonNull
    public List<VacancyModel> getJobs(String request) {
        String jobRequest = request.split(" / ")[0];
        String city = request.split(" / ")[1];
        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        List<VacancyModel> vacancies = null;

        String cityId = cities.getCityId(CityUtils.SITE.JOBSUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.jobs.ua/vacancy/" +
                cityId + "/rabota-" + correctedRequest;

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return vacancies;
        }

        vacancies = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("div[class=\"b-vacancy__top\"]");
        for (Element link : links) {
            String title = link.select("a").attr("title");
            String url = link.select("a").attr("href");
            String date = link.select("span").text();

            VacancyModel vacancyModel = VacancyModel.builder()
                    .setDate(date)
                    .setIsFavorite(false)
                    .setTimeStatus(1) // new
                    .setRequest(request)
                    .setSite(DbContract.SearchSites.SITES[1])
                    .setTitle(title)
                    .setUrl(url)
                    .build();

            vacancies.add(vacancyModel);
        }

        Log.d(TAG, "found " + vacancies.size() + " vacancies");
        return vacancies;
    }
}
