package com.dmelnyk.workinukraine.helpers;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dmitry on 07.03.17.
 */

public class NetUtils {
    private OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build();

    public String getHtmlPage(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) { // response OK
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String replaceSpacesWithPlus(String jobRequest) {
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
}
