package com.dmelnyk.workinukraine.utils.parsing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.models.VacancyContainer;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.db.Tables;
import com.dmelnyk.workinukraine.utils.CityUtils;
import com.dmelnyk.workinukraine.utils.NetUtils;
import com.dmelnyk.workinukraine.utils.di.DaggerUtilComponent;

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
    public ArrayList<VacancyContainer> getJobs(String request) {
        String jobRequest = request.split(" / ")[0];
        String city = request.split(" / ")[1];
        Log.d(TAG, "started getJobs(). City = " + city + " request = " + jobRequest);

        ArrayList<VacancyContainer> vacancies = new ArrayList<>();

        String cityId = cities.getCityId(CityUtils.SITE.WORKUA, city);
        String correctedRequest = netUtils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.work.ua/jobs-" +
                cityId + "-" + correctedRequest;

        String response = netUtils.getHtmlPage(urlRequest);
        if (response == null) {
            Log.e(TAG, "Server not response!");
            return vacancies;
        }

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("h2").select("a");
        for (Element link : links) {
            String title = link.text();
            String url = "https://www.work.ua" + link.attr("href");
            String date = link.attr("title")
                    .substring(title.length() + ", вакансия от ".length());

            VacancyModel vacancyModel = VacancyModel.builder()
                    .setDate(date)
                    .setRequest(request)
                    .setTitle(title)
                    .setUrl(url)
                    .build();

            vacancies.add(VacancyContainer.create(vacancyModel, Tables.SearchSites.TYPE_SITES[4]));
        }
        Log.d(TAG, "found " + vacancies.size() + " vacancies");

        return vacancies;
    }
}
