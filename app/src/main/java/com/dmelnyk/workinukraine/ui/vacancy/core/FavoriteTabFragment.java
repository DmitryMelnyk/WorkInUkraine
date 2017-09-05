package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

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
        createProperAdapter();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        return view;
    }

    private void updateAdapter() {
        if (sCache.isEmpty()) {
            mAdapter = new EmptyFavoriteViewAdapter(null, VacancyCardViewAdapter.TYPE_FAVORITE);
        } else {
            mAdapter = new VacancyCardViewAdapter(
                    (ArrayList<VacancyModel>) sCache, VacancyCardViewAdapter.TYPE_FAVORITE);
            mAdapter.setOnAdapterInteractionListener(this);
        }
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    public void updateData(List<VacancyModel> vacancies) {
        Log.e("222", "FavoriteTabFragment: Creating new adapter");
        sCache = new ArrayList<>(vacancies);
        updateAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
}
