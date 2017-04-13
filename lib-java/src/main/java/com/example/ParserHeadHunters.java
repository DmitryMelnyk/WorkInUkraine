package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by dmitry on 06.03.17.
 */

public class ParserHeadHunters {

    public ArrayList<Job> getJobs(String city, String jobRequest) {
        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = Cities.getCityId(Cities.SITE.HEADHUNTERSUA, city);
        String correctedRequest = Utils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://hh.ua/search/vacancy?text=" +
                correctedRequest + "&area=" + cityId + "&order_by=publication_time";


        String response = Utils.getHtmlPage(urlRequest);
        if (response == null) return null;

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

            Job job = new Job(title, date, url);
            jobs.add(job);
        }

        return jobs;
    }
}
