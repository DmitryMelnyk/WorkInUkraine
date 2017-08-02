package com.dmelnyk.workinukraine.ui.vacancy.core;

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
import com.dmelnyk.workinukraine.data.VacancyModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

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
    private static final String ARG_CARDTYPE = "card adapter type";

    @StringDef({ FRAGMENT_FAVORITE, FRAGMENT_NEW, FRAGMENT_RECENT })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FragmentType {}

    private static int sCardViewAdapterType;
    private static final String ARG_ITEMS = "items";
//    private static final String ARG_FRAGMENT_TYPE = "fragment type";
    private OnFragmentInteractionListener mListener;
    private ArrayList<VacancyModel> mItems;
    private VacancyCardViewAdapter mAdapter;

    public static BaseTabFragment getNewInstance(ArrayList<VacancyModel> items,
                                                 @FragmentType String fragmentType) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ITEMS, items);
        int cardType = fragmentType == FRAGMENT_FAVORITE
                ? VacancyCardViewAdapter.TYPE_FAVORITE
                : VacancyCardViewAdapter.TYPE_TABVIEW;
        args.putInt(ARG_CARDTYPE, cardType);
        BaseTabFragment fragment = new BaseTabFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItems = getArguments().getParcelableArrayList(ARG_ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener = (OnFragmentInteractionListener) getContext();
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        int cardAdapterType = getArguments().getInt(ARG_CARDTYPE);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mAdapter = new VacancyCardViewAdapter(mItems, cardAdapterType);
        mAdapter.setOnAdapterInteractionListener(this);
        recyclerView.setAdapter(mAdapter);

        return view;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onAdapterInteractionItemClicked(VacancyModel vacancyClicked) {
        mListener.onFragmentInteractionItemClicked(vacancyClicked);
    }

    @Override
    public void onAdapterInteractionPopupMenuClicked(
            VacancyModel vacancyClicked,
            @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        mListener.onFragmentInteractionPopupMenuClicked(vacancyClicked, type);
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
        void onFragmentInteractionItemClicked(VacancyModel vacancyModel);

        void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                   @VacancyCardViewAdapter.VacancyPopupMenuType int type);
    }

    public void updateData(ArrayList<VacancyModel> newVacancies) {

        mItems.clear();
        mItems.addAll(newVacancies);

        mAdapter.notifyDataSetChanged();
    }


}
