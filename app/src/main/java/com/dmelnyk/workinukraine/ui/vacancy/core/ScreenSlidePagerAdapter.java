package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.Tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/31/17.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAB_FAVORITE = Tables.SearchSites.FAVORITE;
    private static final String TAB_NEW = Tables.SearchSites.NEW;
    private static final String TAB_RECENT = Tables.SearchSites.RECENT;

    private String[] mTitles;
    private final Map<String, List<VacancyModel>> mBaseFragmentData;
    private final Map<String, List<VacancyModel>> mSitesData;
    private BaseTabFragment mFavoriteTab;

    public ScreenSlidePagerAdapter(
            FragmentManager fm,
            String[] mTitles,
            Map<String, Map<String, List<VacancyModel>>> mAllVacancies) {
        super(fm);
        this.mTitles = mTitles;
        this.mSitesData = mAllVacancies.get(IVacancyInteractor.DATA_TAB_SITES);
        this.mBaseFragmentData = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS);
    }

    private int[] countVacancies() {
        int[] vacanciesCount = new int[4];
        int siteCount = 0;
        for (Map.Entry<String, List<VacancyModel>> vacancies : mSitesData.entrySet()) {
            siteCount += vacancies.getValue().size();
        }
        vacanciesCount[0] = siteCount;
        vacanciesCount[1] = mBaseFragmentData.get(TAB_NEW).size();
        vacanciesCount[2] = mBaseFragmentData.get(TAB_RECENT).size();
        vacanciesCount[3] = mBaseFragmentData.get(TAB_FAVORITE).size();

        return vacanciesCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SitesTabFragment.getNewInstance(mSitesData);
            case 1:
                return BaseTabFragment.getNewInstance(
                        (ArrayList<VacancyModel>) mBaseFragmentData.get(TAB_NEW),
                        BaseTabFragment.FRAGMENT_NEW);
            case 2:
                return BaseTabFragment.getNewInstance(
                        (ArrayList<VacancyModel>) mBaseFragmentData.get(TAB_RECENT),
                        BaseTabFragment.FRAGMENT_RECENT);
            case 3:
                mFavoriteTab = BaseTabFragment.getNewInstance(
                        (ArrayList<VacancyModel>) mBaseFragmentData.get(TAB_FAVORITE),
                        BaseTabFragment.FRAGMENT_FAVORITE);
                return mFavoriteTab;
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    public void updateFavoriteTab(List<VacancyModel> vacancies) {
        if (mFavoriteTab == null) {
            mBaseFragmentData.put(TAB_FAVORITE, vacancies);
        } else {
            mFavoriteTab.updateData((ArrayList<VacancyModel>) vacancies);
        }
    }
}
