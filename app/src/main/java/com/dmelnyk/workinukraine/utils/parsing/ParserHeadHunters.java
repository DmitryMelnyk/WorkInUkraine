package com.dmelnyk.workinukraine.utils.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.db.DbContract;
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
import javax.inject.Singleton;

/**
 * Created by dmitry on 06.03.17.
 */

public class ParserHeadHunters {
    private final String TAG = this.getClass().getSimpleName();

    NetUtils netUtils = NetUtils.getInstance();

    @Inject CityUtils cities;

    public ParserHeadHunters(Context context) {
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
    @Nullable
    public List<VacancyModel> getJobs(String request) {
        String jobRequest = request.split(" / ")[0];
        String city = request.split(" / ")[1];
        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        List<VacancyModel> vacancies = null;

        String cityId = cities.getCityId(CityUtils.SITE.HEADHUNTERSUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://hh.ua/search/vacancy?text=" +
                correctedRequest + "&area=" + cityId + "&order_by=publication_time";

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return vacancies;
        }

        vacancies = new ArrayList<>();

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("div[class=\"search-result-description__item search-result-description__item_primary\"]");
        for (Element link : links) {
            String date = "";
            for (Element dateContainer : link.select("span")) {
                if (dateContainer.attr("data-qa").equals("vacancy-serp__vacancy-date"))
                    date = dateContainer.text();
            }

            String title = "";
            for (Element titleContainer : link.select("a[href]")) {
                if (titleContainer.attr("data-qa").equals("vacancy-serp__vacancy-title"))
                    title = titleContainer.text();
            }

            String url = link.select("a[href]").attr("href");

            VacancyModel vacancyModel = VacancyModel.builder()
                    .setDate(date)
                    .setIsFavorite(false)
                    .setTimeStatus(1) // new
                    .setRequest(request)
                    .setSite(DbContract.SearchSites.SITES[0])
                    .setTitle(title)
                    .setUrl(url)
                    .build();
            vacancies.add(vacancyModel);
        }

        Log.d(TAG, "found " + vacancies.size() + " vacancies");
        return vacancies;
    }
}
