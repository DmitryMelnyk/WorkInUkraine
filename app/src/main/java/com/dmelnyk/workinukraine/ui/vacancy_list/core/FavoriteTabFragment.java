package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.VacancyListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d264 on 8/22/17.
 */

public class FavoriteTabFragment extends BaseTabFragment {

    private RecyclerView mRecyclerView;
    private VacancyCardViewAdapter mAdapter;
    private static List<VacancyModel> sCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_rv, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e(getClass().getSimpleName(), "onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.e(getClass().getSimpleName(), "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        sCache = ((VacancyListActivity) getActivity()).getFavoritesData();
    }

    private void updateAdapter() {
        Log.e(getClass().getSimpleName(), "updateAdapter()");
        if (sCache == null) sCache = new ArrayList<>();
        if (sCache.isEmpty()) {
            Log.e(getClass().getSimpleName(), "Creating empty adapter");
            mAdapter = new EmptyFavoriteViewAdapter(null, VacancyCardViewAdapter.TYPE_FAVORITE);
        } else {
            Log.e(getClass().getSimpleName(), "Creating non empty adapter");
            mAdapter = new VacancyCardViewAdapter(
                    (ArrayList<VacancyModel>) sCache, VacancyCardViewAdapter.TYPE_FAVORITE);
            mAdapter.setOnAdapterInteractionListener(this);
        }

        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Log.e(getClass().getSimpleName(), "RecyclerView = null");
        }
    }

    public void updateData(List<VacancyModel> vacancies) {
        sCache = new ArrayList<>(vacancies);
        updateAdapter();
    }
}
