package com.dmelnyk.workinukraine.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by dmitry on 07.03.17.
 */

public class NetUtils {

    private static NetUtils singleton;

    private NetUtils() { }

    public static NetUtils getInstance() {
        if (singleton == null) {
             singleton = new NetUtils();
        }
        return singleton;
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();

    public String getHtmlPage(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Call call = client.newCall(request);
            Response response = call.execute();
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

    public static boolean isNetworkReachable(Context context) {
        final ConnectivityManager cManager =
                (ConnectivityManager) context.getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo current = cManager.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.getState() == NetworkInfo.State.CONNECTED
                && ((current.getType() == cManager.TYPE_MOBILE
                || current.getType() == cManager.TYPE_WIFI)));
    }
}
