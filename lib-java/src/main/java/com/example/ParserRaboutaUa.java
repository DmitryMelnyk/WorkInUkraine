package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserRaboutaUa {
    final int year = Calendar.getInstance().get(Calendar.YEAR);

    /**
     * Parse page with given jobRequest parameters.
     * @param city - city field.
     * @param jobRequest - search parameter
     * @return list of Job elements or empty ArrayList
     */
    public ArrayList<Job> getJobs(String city, String jobRequest) {
        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = Cities.getCityId(Cities.SITE.RABOTAUA, city);
        String correctedRequest = Utils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "https://rabota.ua/jobsearch/vacancy_list?regionId=" +
                cityId + "&keyWords=" + correctedRequest;


        String response = Utils.getHtmlPage(urlRequest);
        if (response == null) return null;

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("tr");
        for (Element link : links) {

            String date = link.select("p[class=\"f-vacancylist-agotime f-text-light-gray fd-craftsmen\"]").text();
            String title = link.select("a[href]").text();
            String url = link.select("a[href]").attr("href");
            if (url.contains("/vacancy") && url.contains("/company")) {
                String fullUrl = "https://rabota.ua" + url;
                Job job = new Job(title, date, fullUrl);
                jobs.add(job);
            }
        }

        return jobs;
    }

    public static void main(String[] args) {
        System.out.println(
                new ParserRaboutaUa().getJobs("Киев", "android developer")
        );
    }
}
