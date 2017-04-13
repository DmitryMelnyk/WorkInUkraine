package com.example;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry on 08.03.17.
 */

public class Cities {

    public enum SITE {
        WORKUA, RABOTAUA, JOBSUA, HEADHUNTERSUA, WORKNEWINFO;
    }

    private static String WORKUA = "work.ua";
    private static String RABOTAUA = "rabota.ua";
    private static String JOBSUA = "jobs.ua";
    private static String HEADHUNTERSUA = "hh.ua";
    private static String WORKNEWINFO = "worknew.info";

    private static String readJsonFromFile() {
        StringBuilder text = new StringBuilder();
        File file = new File("/home/dmitry/sites_key.json");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(text);
        return text.toString();
    }

    private static HashMap<SITE, Map<String, String>> parseJsonToMap(String json) {
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

            return allMaps;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<SITE, Map<String, String>> siteMap;
    public static String getCityId(SITE site, String city) {
        if (siteMap == null) {
            siteMap = parseJsonToMap(readJsonFromFile());
        }

        return siteMap.get(site).get(city);
    }
}
