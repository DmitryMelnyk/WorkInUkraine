package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by dmitry on 07.03.17.
 */

public class ParserWorkNewInfo {

    public ArrayList<Job> getJobs(String city, String jobRequest) {
        ArrayList<Job> jobs = new ArrayList<>();

        String cityId = Cities.getCityId(Cities.SITE.WORKNEWINFO, city);
        String correctedRequest = Utils.replaceSpacesWithPlus(jobRequest);
        String urlRequest = "http://worknew.info/job/search/?q=" + correctedRequest +
                "&ct=" + cityId + "&s=0|0|1";

        String response = Utils.getHtmlPage(urlRequest);
        if (response == null) return null;

        Document doc = Jsoup.parse(response);
        Elements links = doc.getElementsByTag("div").select("li[class=even]");
        for (Element link : links) {
            String urlRaw =  link.select("a").attr("href");
            String url = "http://worknew.info" + urlRaw;
            String date = link.select("span[class=svadded]").select("b").text();
            String title = link.select("a[href=" + urlRaw + "]").text().replace(" ... →", "");
            Job job = new Job(title, date, url);
            jobs.add(job);
        }

        return jobs;
    }
}
