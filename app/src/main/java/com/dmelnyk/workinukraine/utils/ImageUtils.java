package com.dmelnyk.workinukraine.helpers;

import com.dmelnyk.workinukraine.R;

import java.util.HashMap;

/**
 * Created by dmitry on 19.03.17.
 */

public class ImageUtils {
    HashMap<String, Integer> titleImage = new HashMap<>();

    public Integer getImageId(String web) {
        return titleImage.get(web);
    }

    public ImageUtils() {
        titleImage.put("hh.ua", R.drawable.bg_hhua);
        titleImage.put("jobs.ua", R.drawable.bg_jobsua);
        titleImage.put("rabota.ua", R.drawable.bg_rabotaua);
        titleImage.put("worknew.info", R.drawable.bg_worknewinfo);
        titleImage.put("work.ua", R.drawable.bg_workua);
    }
}
