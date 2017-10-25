package com.dmelnyk.workinukraine.utils;

import com.dmelnyk.workinukraine.R;

/**
 * Created by d264 on 10/24/17.
 */

public class ButtonTabUtil {

    public static int[][] getResources(int buttonTabType) {
        int[][] resource;
        if (buttonTabType == 2) {
            resource = new int[4][2]; // 4 buttons (all, new, recent, favorite)
        } else {
            resource = new int[3][2]; // 3 buttons (all, new / recent, favorite)
        }

        resource[0][0] = R.mipmap.ic_tab_vacancy_light;
        resource[0][1] = R.mipmap.ic_tab_vacancy_dark;

        switch (buttonTabType) {
            case 1: // all, new, favorite
                resource[1][0] = R.mipmap.ic_tab_new_light;
                resource[1][1] = R.mipmap.ic_tab_new_dark;
                resource[2][0] = R.mipmap.ic_tab_favorite_light;
                resource[2][1] = R.mipmap.ic_tab_favorite_dark;
                break;
            case 2: // all, new, recent, favorite
                resource[1][0] = R.mipmap.ic_tab_new_light;
                resource[1][1] = R.mipmap.ic_tab_new_dark;
                resource[2][0] = R.mipmap.ic_tab_recent_light;
                resource[2][1] = R.mipmap.ic_tab_recent_dark;
                resource[3][0] = R.mipmap.ic_tab_favorite_light;
                resource[3][1] = R.mipmap.ic_tab_favorite_dark;
                break;
            case 3: // all, recent, favorite
                resource[1][0] = R.mipmap.ic_tab_recent_light;
                resource[1][1] = R.mipmap.ic_tab_recent_dark;
                resource[2][0] = R.mipmap.ic_tab_favorite_light;
                resource[2][1] = R.mipmap.ic_tab_favorite_dark;
                break;
        }

        return resource;
    }
}
