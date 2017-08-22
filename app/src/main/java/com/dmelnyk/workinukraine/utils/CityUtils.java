package com.dmelnyk.workinukraine.utils;

import android.content.Context;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by dmitry on 08.03.17.
 */

public class CityUtils {

    private static final String TAG = "GT.CityUtils";

    public enum SITE {
        WORKUA, RABOTAUA, JOBSUA, HEADHUNTERSUA, WORKNEWINFO;
    }

    public static String WORKUA = "work.ua";
    public static String RABOTAUA = "rabota.ua";
    public static String JOBSUA = "jobs.ua";
    public static String HEADHUNTERSUA = "hh.ua";
    public static String WORKNEWINFO = "worknew.info";
    public static Context mContext;
    public static CityUtils cities;

    public ArrayList<String> citiesList = new ArrayList<>();

    public CityUtils(Context context) {
        mContext = context;
    }

//    public CityUtils() {}

    private String readJsonFromFile() {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(mContext.getAssets().open("files/sites_key.json")));
            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    private HashMap<SITE, Map<String, String>> parseJsonToMap(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONObject objectWORKUA = object.getJSONObject(WORKUA);
            JSONObject objectRABOTAUA = object.getJSONObject(RABOTAUA);
            JSONObject objectJOBSUA = object.getJSONObject(JOBSUA);
            JSONObject objectHEADHUNTERSUA = object.getJSONObject(HEADHUNTERSUA);
            JSONObject objectWORKNEWINFO = object.getJSONObject(WORKNEWINFO);

            final Moshi build = new Moshi.Builder().build();
            final JsonAdapter<Map> adapter = build.adapter(Map.class);
            Map<String, String> mapWORKUA = adapter.fromJson(objectWORKUA.toString());
            Map<String, String> mapRABOTAUA = adapter.fromJson(objectRABOTAUA.toString());
            Map<String, String> mapJOBSUA = adapter.fromJson(objectJOBSUA.toString());
            Map<String, String> mapHEADHUNTERSUA = adapter.fromJson(objectHEADHUNTERSUA.toString());
            Map<String, String> mapWORKNEWINFO = adapter.fromJson(objectWORKNEWINFO.toString());

            HashMap<SITE, Map<String, String>> allMaps = new HashMap<>();
            allMaps.put(SITE.HEADHUNTERSUA, mapHEADHUNTERSUA);
            allMaps.put(SITE.JOBSUA, mapJOBSUA);
            allMaps.put(SITE.RABOTAUA, mapRABOTAUA);
            allMaps.put(SITE.WORKUA, mapWORKUA);
            allMaps.put(SITE.WORKNEWINFO, mapWORKNEWINFO);

            // create cities-list for spinner
            createCityList(mapWORKUA.keySet());
            return allMaps;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createCityList(Collection<String> values) {
        TreeSet<String> treeSet = new TreeSet<>(values);
        citiesList.add("Киев");
        treeSet.remove("Киев");
        citiesList.addAll(treeSet);
        Log.d(TAG, "citiesList = " + citiesList);
    }

    private static HashMap<SITE, Map<String, String>> siteMap;

    public String getCityId(SITE site, String city) {
        if (siteMap == null) {
            siteMap = parseJsonToMap(readJsonFromFile());
        }

        return siteMap.get(site).get(city);
    }

    public ArrayList<String> getCities() {
        parseJsonToMap(readJsonFromFile());
        return citiesList;
    }
}
