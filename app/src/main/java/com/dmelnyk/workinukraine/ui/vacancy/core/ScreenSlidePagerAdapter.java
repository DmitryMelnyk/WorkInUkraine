package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/31/17.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAB_FAVORITE = IVacancyInteractor.VACANCIES_FAVORITE;
    private static final String TAB_NEW = IVacancyInteractor.VACANCIES_NEW;
    private static final String TAB_RECENT = IVacancyInteractor.VACANCIES_RECENT;

    private final SitesTabFragment mFragment0;
    private final BaseTabFragment mFragment1;
    private FavoriteTabFragment mFragment2;

    private String[] mTitles;
    private final Map<String, List<VacancyModel>> mBaseFragmentData;
    private final Map<String, List<VacancyModel>> mSitesData;

    public ScreenSlidePagerAdapter(
            FragmentManager fm,
            String[] mTitles,
            Map<String, Map<String, List<VacancyModel>>> mAllVacancies) {
        super(fm);

        Log.e("222", "ScreenSlidePagerAdapter: running constructor");
        this.mTitles = mTitles;
        this.mSitesData = mAllVacancies.get(IVacancyInteractor.DATA_TAB_SITES);
        this.mBaseFragmentData = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS);

        mFragment0 = SitesTabFragment.getNewInstance(mSitesData);

        List<VacancyModel> newVacancies = mBaseFragmentData.get(TAB_NEW);
        List<VacancyModel> recentVacancies = mBaseFragmentData.get(TAB_RECENT);
        if (newVacancies != null && !newVacancies.isEmpty()) {
            mFragment1 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) newVacancies,
                    BaseTabFragment.FRAGMENT_NEW);
        } else {
            mFragment1 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) recentVacancies,
                    BaseTabFragment.FRAGMENT_RECENT);
        }

        mFragment2 = new FavoriteTabFragment();
        mFragment2.updateData(mBaseFragmentData.get(TAB_FAVORITE));
    }

    @Override
    public int getItemPosition(Object object) {
        int itemPosition;
        if (object instanceof FavoriteTabFragment) {
            itemPosition = POSITION_NONE;
        } else {
            itemPosition = POSITION_UNCHANGED;
        }

        Log.e("222", "getItemPosition=" + itemPosition + " for object=" + object);
        return itemPosition;
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("222", "getItem =" + position);

        switch (position) {
            case 0:
                return mFragment0;
            case 1:
                return mFragment1;
            case 2:
                return mFragment2;
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e("222", "SSPAdapter. instantiateItem =" + position);
        return super.instantiateItem(container, position);
    }

    public void updateFavoriteData(List<VacancyModel> vacancies) {
        Log.e("222", "SSPAdapter. updateFavoriteData =" + vacancies.size());
        mBaseFragmentData.put(TAB_FAVORITE, vacancies);
        mFragment2.updateData(vacancies);
    }
}
