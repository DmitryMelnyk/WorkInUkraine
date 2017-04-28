package com.dmelnyk.workinukraine.mvp.activity_tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.helpers.CardViewAdapter;
import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.Job;

import java.util.ArrayList;

/**
 * Created by dmitry on 14.03.17.
 */

public class TabRecycler extends Fragment {

    public static final String KEY_JOB_LIST = "TabRecycler.JOBLIST";

    RecyclerView recycler;
    private ArrayList<Job> jobs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        // extracting data
        Bundle args = getArguments();
        jobs = args.getParcelableArrayList(KEY_JOB_LIST);

        view = inflater.inflate(R.layout.recycler_view, container, false);
        recycler = (RecyclerView) view.findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.Adapter adapter = new CardViewAdapter(jobs, getContext(), CardViewAdapter.TABVIEW);
        recycler.setAdapter(adapter);

        return view;
    }
}
