package com.example;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dmitry on 07.03.17.
 */

public class Utils {
    private static OkHttpClient client = new OkHttpClient();

    public static String getHtmlPage(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) { // response OK
                return response.body().string();
            }
        } catch (IOException e) {
            // NOP
//            e.printStackTrace();
        }
        return null;
    }

    public static String replaceSpacesWithPlus(String jobRequest) {
        String[] splitterString = jobRequest.split(" ");
        // if request contains only one word - return it
        if (splitterString.length == 1) return jobRequest;

        String resultRequest = splitterString[0];
        int i = 1;
        do {
            resultRequest += "+" + splitterString[i];
            i++;
        } while (i < splitterString.length);
        return resultRequest;
    }

    public static void main(String[] args) {
        System.out.println(
                new ParserHeadHunters().getJobs("Мариуполь", "офис менеджер")
        );
    }
}
