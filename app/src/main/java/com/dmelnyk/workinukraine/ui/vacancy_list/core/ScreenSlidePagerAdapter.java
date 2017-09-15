package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.business.vacancy_list.IVacancyListInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.SiteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 7/31/17.
 */

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private final SitesTabFragment mFragment0;
    private Fragment mFragment1;
    private Fragment mFragment2;
    private Fragment mFragment3;

    private String[] mTitles;
    private final Map<String, List<VacancyModel>> mSitesData;
    private final List<VacancyModel> mNewFragmentData;
    private final List<VacancyModel> mResentFragmentData;
    private List<VacancyModel> mFavoriteFragmentData;

    public ScreenSlidePagerAdapter(
            FragmentManager fm,
            String[] mTitles,
            Map<String, List<VacancyModel>> mAllVacancies) {
        super(fm);

        this.mTitles = mTitles;
        this.mSitesData = SiteUtil.convertToSiteMap(mAllVacancies.get(IVacancyListInteractor.DATA_ALL));
        this.mNewFragmentData = mAllVacancies.get(IVacancyListInteractor.DATA_NEW);
        this.mResentFragmentData = mAllVacancies.get(IVacancyListInteractor.DATA_RECENT);
        this.mFavoriteFragmentData = mAllVacancies.get(IVacancyListInteractor.DATA_FAVORITE);

        mFragment0 = SitesTabFragment.getNewInstance(mSitesData);

        if (!mNewFragmentData.isEmpty() && mResentFragmentData.isEmpty()) {
            mFragment1 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) mNewFragmentData,
                    BaseTabFragment.FRAGMENT_NEW);
            mFragment2 = new FavoriteTabFragment();
            ((FavoriteTabFragment) mFragment2).updateData(mFavoriteFragmentData);
        }

        if (!mNewFragmentData.isEmpty() && !mResentFragmentData.isEmpty()) {
            mFragment1 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) mNewFragmentData,
                    BaseTabFragment.FRAGMENT_NEW);
            mFragment2 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) mResentFragmentData,
                    BaseTabFragment.FRAGMENT_RECENT);
            mFragment3 = new FavoriteTabFragment();
            ((FavoriteTabFragment) mFragment3).updateData(mFavoriteFragmentData);
        }

        if (mNewFragmentData.isEmpty()) {
            mFragment1 = BaseTabFragment.getNewInstance(
                    (ArrayList<VacancyModel>) mResentFragmentData,
                    BaseTabFragment.FRAGMENT_RECENT);
            mFragment2 = new FavoriteTabFragment();
            ((FavoriteTabFragment) mFragment2).updateData(mFavoriteFragmentData);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        int itemPosition;
        if (object instanceof FavoriteTabFragment) {
            itemPosition = POSITION_NONE;
        } else {
            itemPosition = POSITION_UNCHANGED;
        }

        return itemPosition;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mFragment0;
            case 1:
                return mFragment1;
            case 2:
                return mFragment2;
            case 3:
                if (mFragment3 != null) {
                    return mFragment3;
                } else {
                    return null;
                }
        }

        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    public void updateFavoriteData(List<VacancyModel> vacancies) {
        mFavoriteFragmentData = vacancies;
        if (mFragment2 instanceof FavoriteTabFragment) {
            ((FavoriteTabFragment) mFragment2).updateData(vacancies);
        } else {
            ((FavoriteTabFragment) mFragment3).updateData(vacancies);
        }
    }
}
