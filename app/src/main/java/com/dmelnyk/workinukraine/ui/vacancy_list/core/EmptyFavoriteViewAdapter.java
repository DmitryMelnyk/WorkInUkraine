package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.ArrayList;

/**
 * Created by d264 on 8/18/17.
 */

class EmptyFavoriteViewAdapter extends VacancyCardViewAdapter {

    public EmptyFavoriteViewAdapter(ArrayList<VacancyModel> vacancies, int cardViewType) {
        super(vacancies, cardViewType);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_favorite_empty, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
