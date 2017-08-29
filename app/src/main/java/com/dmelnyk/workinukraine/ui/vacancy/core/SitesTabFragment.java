package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.h6ah4i.android.tablayouthelper.TabLayoutHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SitesTabFragment extends Fragment {

    private static Map<String, List<VacancyModel>> sVacancies;
    private static OnFragmentInteractionListener mListener;
    private static List<String> sSitesTitle;
    private ViewPager mViewPager;

    public static SitesTabFragment getNewInstance(Map<String, List<VacancyModel>> vacanciesMap) {

        sVacancies = vacanciesMap;
        sSitesTitle = new ArrayList<>();
        for(String title : vacanciesMap.keySet()) {
            sSitesTitle.add(title);
        }
        SitesTabFragment fragment = new SitesTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.vacancy_fragment_tab, container, false);
        // Sites titles
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(sSitesTitle, sVacancies);
        mViewPager.setAdapter(adapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        TabLayout tabLayout = (TabLayout)(view.findViewById(R.id.tab_layout));
        TabLayoutHelper tabLayoutHelper = new TabLayoutHelper(tabLayout, mViewPager);
        tabLayoutHelper.setAutoAdjustTabModeEnabled(true);

        return view;
    }

    /*
     * Simple PagerAdapter to display page views with RecyclerView
     */
    private class TabsPagerAdapter extends PagerAdapter implements
            VacancyCardViewAdapter.OnAdapterInteractionListener {
        private List<String> mTitles;
        private final Map<String, List<VacancyModel>> mData;

        public TabsPagerAdapter(List<String> titles,
                                Map<String, List<VacancyModel>> data) {
            mTitles = titles;
            mData = data;

            Log.e("!!!", "SitesTabFragment Adapter created.");
        }

        /*
         * SlidingTabLayout requires thie method to define the
         * text that each tab will display.
         */

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            VacancyCardViewAdapter adapter = new VacancyCardViewAdapter(
                    (ArrayList<VacancyModel>)  mData.get(mTitles.get(position)),
                    VacancyCardViewAdapter.TYPE_STANDARD);
            adapter.setOnAdapterInteractionListener(this);

            RecyclerView recyclerView = new RecyclerView(container.getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
            recyclerView.setAdapter(adapter);

            container.addView(recyclerView);
            return recyclerView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public void onAdapterInteractionItemClicked(VacancyModel vacancyClicked, View bodyTextView) {
            mListener.onFragmentInteractionItemClicked(vacancyClicked, bodyTextView);
        }

        @Override
        public void onAdapterInteractionPopupMenuClicked(
                VacancyModel vacancyClicked, @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
            mListener.onFragmentInteractionPopupMenuClicked(vacancyClicked, type);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("222", "onResume SitesTabFragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteractionItemClicked(VacancyModel vacancyModel, View bodyTextView);

        void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                   @VacancyCardViewAdapter.VacancyPopupMenuType int type);
    }
}
