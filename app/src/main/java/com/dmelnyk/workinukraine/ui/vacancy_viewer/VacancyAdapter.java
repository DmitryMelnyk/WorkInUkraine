package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by d264 on 9/8/17.
 */

class VacancyAdapter extends FragmentStatePagerAdapter {
    private final List<VacancyModel> mVacancies;
    private final Map<Integer, VacancyFragment> fragmentMap;

    public VacancyAdapter(FragmentManager fm, List<VacancyModel> vacancies) {
        super(fm);
        this.mVacancies = vacancies;
        fragmentMap = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        VacancyFragment fragment =  VacancyFragment.getNewInstance(mVacancies.get(position));
        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return mVacancies == null ? 0 : mVacancies.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentMap.remove(position);
        super.destroyItem(container, position, object);
    }

    public VacancyFragment getFragmentReference(int position) {
        return fragmentMap.get(position);
    }
}
