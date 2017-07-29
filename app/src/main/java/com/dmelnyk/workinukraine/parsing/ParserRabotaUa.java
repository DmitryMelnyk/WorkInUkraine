package com.dmelnyk.workinukraine.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.di.component.DaggerUtilComponent;
import com.dmelnyk.workinukraine.helpers.CityUtils;
import com.dmelnyk.workinukraine.helpers.NetUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserRabotaUa {
    private static final String TAG = "TAG.ParserRabotaUa";
    final int year = Calendar.getInstance().get(Calendar.YEAR);

    @Inject
    NetUtils netUtils;

    @Inject
    CityUtils cities;

    public ParserRabotaUa(Context context) {
        DaggerUtilComponent.builder()
                .applicationComponent(WorkInUaApplication.get(context).getAppComponent())
                .build()
                .inject(this);
    }

    /**
     * Parse page with given jobRequest parameters.
     * @param request - Request in format "search request / city".
     * @return list of VacancyModule's or empty ArrayList
     */
    @NonNull
    public ArrayList<VacancyModel> getJobs(String request) {
        String jobRequest = request.split(" / ")[0];
        String city = request.split(" / ")[1];

        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        ArrayList<VacancyModel> vacancies = new ArrayList<>();

        String cityId = cities.getCityId(CityUtils.SITE.RABOTAUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://rabota.ua/jobsearch/vacancy_list?regionId=" +
                cityId + "&keyWords=" + correctedRequest;


        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return vacancies;
        }

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("tr");
        for (Element link : links) {

            String date = link.select("p[class=\"f-vacancylist-agotime f-text-light-gray fd-craftsmen\"]").text();
            String title = link.select("a[href]").text();
            String shortUrl = link.select("a[href]").attr("href");
            if (shortUrl.contains("/vacancy") && shortUrl.contains("/company")) {
                String url = "https://rabota.ua" + shortUrl;

                VacancyModel vacancyModel = VacancyModel.builder()
                        .setDate(date)
                        .setRequest(request)
                        .setTitle(title)
                        .setUrl(url)
                        .build();
                vacancies.add(vacancyModel);
            }
        }

        Log.d(TAG, "found " + vacancies.size() + " vacancies");
        return vacancies;
    }
}
