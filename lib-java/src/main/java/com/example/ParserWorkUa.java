package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserWorkUa {

    public ArrayList<Job> getJobs(String city, String jobRequest) {
        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = Cities.getCityId(Cities.SITE.WORKUA, city);
        String correctedRequest = Utils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://www.work.ua/jobs-" +
                cityId + "-" + correctedRequest;

        String response = Utils.getHtmlPage(urlRequest);
        if (response == null) return null;
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

        System.out.println(jobs.size());
        return jobs;
    }
}
