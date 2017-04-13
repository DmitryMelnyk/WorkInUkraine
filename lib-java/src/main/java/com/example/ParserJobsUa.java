package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserJobsUa {

    public ArrayList<Job> getJobs(String city, String jobRequest) {
        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = Cities.getCityId(Cities.SITE.JOBSUA, city);
        String correctedRequest = Utils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.jobs.ua/vacancy/" +
                cityId + "/rabota-" + correctedRequest;

        String response = Utils.getHtmlPage(urlRequest);
        if (response == null) return null;

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("div[class=\"b-vacancy__top\"]");
        for (Element link : links) {
            String title = link.select("a").attr("title");
            String url = link.select("a").attr("href");
            String date = link.select("span").text();
            Job job = new Job(title, date, url);
            jobs.add(job);
        }

        return jobs;
    }
}
