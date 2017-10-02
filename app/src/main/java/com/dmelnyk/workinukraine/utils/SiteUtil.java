package com.dmelnyk.workinukraine.utils;

import com.dmelnyk.workinukraine.db.DbContract;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 9/15/17.
 */

public class SiteUtil {

    public static Map<String, List<VacancyModel>> convertToSiteMap(List<VacancyModel> vacancyContainerList) {

        Map<String, List<VacancyModel>> sites = new LinkedHashMap<>();
        List<VacancyModel>[] siteList = new List[5];
        for (int i = 0; i < siteList.length; i++) {
            siteList[i] = new ArrayList<>();
        }

        for (VacancyModel vacancy : vacancyContainerList) {
            for (int i = 0; i < DbContract.SearchSites.SITES.length; i++) {
                if (vacancy.site().equals(DbContract.SearchSites.SITES[i])) {
                    siteList[i].add(vacancy);
                    break;
                }
            }
        }

        // Sorts lists in descending order
        sortCollections(siteList);

        for (int i = 0; i < siteList.length; i++) {
            if (siteList[i] != null && !siteList[i].isEmpty())
                sites.put(siteList[i].get(0).site(), siteList[i]);
        }

        return sites;
    }

    private static void sortCollections(List<VacancyModel>[] siteList) {
        Arrays.sort(siteList, new Comparator<List<VacancyModel>>() {
            @Override
            public int compare(List<VacancyModel> l1, List<VacancyModel> l2) {
                return l2.size() - l1.size();
            }
        });
    }
}
