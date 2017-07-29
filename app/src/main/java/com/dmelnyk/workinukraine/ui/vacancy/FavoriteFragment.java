package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.VacancyModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FavoriteFragment extends Fragment implements
        CardViewAdapter.OnAdapterInteractionListener{

    private static final String ARG_ITEMS = "items";
    private static Context mContext;
    private OnFragmentInteractionListener mListener;
    private ArrayList<VacancyModel> mItems;
    private CardViewAdapter mAdapter;

    public static FavoriteFragment getNewInstance(ArrayList<VacancyModel> items, Context context) {
        mContext = context;
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, items);
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mItems = getArguments().getParcelableArrayList(ARG_ITEMS);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext/*container.getContext()*/));
        mAdapter = new CardViewAdapter(mItems, CardViewAdapter.MENU_TYPE_TABVIEW);
        mAdapter.setOnAdapterInteractionListener(this);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAdapterInteractionItemClicked(int position) {
        mListener.onFragmentInteractionItemClicked(position);
    }

    @Override
    public void onAdapterInteractionPopupMenuClicked(int position,
            @CardViewAdapter.VacancyPopupMenuType int type) {
        mListener.onFragmentInteractionPopupMenuClicked(position, type);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteractionItemClicked(int position);

        void onFragmentInteractionPopupMenuClicked(int position, int type);
    }

    public void setFavoriteFragmentInteractionListener(OnFragmentInteractionListener listener) {
        mListener = listener;
    }

    public void updateData(ArrayList<VacancyModel> newVacancies) {
        List<VacancyModel> removedVacancyList = new ArrayList<VacancyModel>(mItems);
        removedVacancyList.removeAll(newVacancies);
        for (VacancyModel removedVacancy : removedVacancyList) {
            mItems.remove(removedVacancy);
        }

        mAdapter.notifyDataSetChanged();
    }
}
