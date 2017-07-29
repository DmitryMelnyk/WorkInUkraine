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

import javax.inject.Inject;

/**
 * Created by dmitry on 06.03.17.
 */

public class ParserHeadHunters {
    private final static String TAG = "TAG.ParserHeadHunters";

    @Inject
    NetUtils netUtils;

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
     */
    @NonNull
    public ArrayList<VacancyModel> getJobs(String request) {
        String jobRequest = request.split(" / ")[0];
        String city = request.split(" / ")[1];
        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        ArrayList<VacancyModel> vacancies = new ArrayList<>();

        String cityId = cities.getCityId(CityUtils.SITE.HEADHUNTERSUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://hh.ua/search/vacancy?text=" +
                correctedRequest + "&area=" + cityId + "&order_by=publication_time";

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return vacancies;
        }

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
                    .setRequest(request)
                    .setTitle(title)
                    .setDate(date)
                    .setUrl(url)
                    .build();
//            VacancyModule vacancyModel = VacancyModule.builder()
//                    (title, date, url);
            vacancies.add(vacancyModel);
        }

        Log.d(TAG, "found " + vacancies.size() + " vacancies");
        return vacancies;
    }
}
