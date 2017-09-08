package com.dmelnyk.workinukraine.ui.vacancy_webview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d264 on 9/8/17.
 */

class VacancyAdapter extends FragmentStatePagerAdapter {
    private final List<VacancyModel> mVacancies;

    public VacancyAdapter(FragmentManager fm, List<VacancyModel> vacancies) {
        super(fm);
        this.mVacancies = vacancies;
    }

    @Override
    public Fragment getItem(int position) {
        return VacancyFragment.getNewInstance(mVacancies.get(position));
    }

    @Override
    public int getCount() {
        return mVacancies.size();
    }
}
