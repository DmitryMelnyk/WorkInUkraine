package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class BaseTabFragment extends Fragment implements
        VacancyCardViewAdapter.OnAdapterInteractionListener{

    public static final String FRAGMENT_FAVORITE = "favorite";
    public static final String FRAGMENT_NEW = "new";
    public static final String FRAGMENT_RECENT = "recent";
    public static final String ARG_CARD_TYPE = "card_adapter_type";

    private RecyclerView mRecyclerView;
    private int mCardAdapterType;

    @StringDef({ FRAGMENT_FAVORITE, FRAGMENT_NEW, FRAGMENT_RECENT })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FragmentType {}

    private static final String ARG_ITEMS = "items";
    private OnFragmentInteractionListener mListener;
    private ArrayList<VacancyModel> mItems;
    private VacancyCardViewAdapter mAdapter;

    public static BaseTabFragment getNewInstance(ArrayList<VacancyModel> items,
                                                 @FragmentType String fragmentType) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, items);
        int cardType = fragmentType == FRAGMENT_FAVORITE
                ? VacancyCardViewAdapter.TYPE_FAVORITE
                : fragmentType == FRAGMENT_NEW
                ? VacancyCardViewAdapter.TYPE_NEW
                : VacancyCardViewAdapter.TYPE_RECENT;
        args.putInt(ARG_CARD_TYPE, cardType);
        BaseTabFragment fragment = new BaseTabFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SitesTabFragment.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mItems = getArguments().getParcelableArrayList(ARG_ITEMS);
        mCardAdapterType = getArguments().getInt(ARG_CARD_TYPE);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_base_rv, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createProperAdapter();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Creates and updates adapter
     */
    public void createProperAdapter() {
        if (mCardAdapterType == VacancyCardViewAdapter.TYPE_FAVORITE && mItems.isEmpty()) {
            mAdapter = new EmptyFavoriteViewAdapter(null, mCardAdapterType);
        } else {
            mAdapter = new VacancyCardViewAdapter(mItems, mCardAdapterType);
            mAdapter.setOnAdapterInteractionListener(this);
        }
    }

    @Override
    public void onAdapterInteractionItemClicked(VacancyModel vacancyClicked) {
        mListener.onFragmentInteractionItemClicked(vacancyClicked);
    }

    @Override
    public void onAdapterInteractionPopupMenuClicked(
            VacancyModel vacancyClicked,
            @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        // Removing or adding vacancy to favorite list
        mListener.onFragmentInteractionPopupMenuClicked(vacancyClicked, type);
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteractionItemClicked(VacancyModel vacancyModel);

        void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                   @VacancyCardViewAdapter.VacancyPopupMenuType int type);
    }
}
