package com.dmelnyk.workinukraine.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.dmelnyk.workinukraine.ui.search.SearchFragment;

/**
 * Created by d264 on 9/6/17.
 */

public class BaseFragment extends Fragment {

    private OnFragmentInteractionListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mCallback = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BaseFragment.OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        closeMainMenuCallback();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * Opens main menu of NavigationActivity and minimize Fragment
     */
    protected void openMainMenuCallback() {
        mCallback.onOpenMainMenuCallback();
    }

    /**
     * Closes main menu of NavigationActivity and maximize Fragment
     * This method is called after resuming Fragment when the view
     * is fully created.
     */
    protected void closeMainMenuCallback() {
        mCallback.onCloseMainMenuCallback();
    }

    public interface OnFragmentInteractionListener {
        // for open NavigationDrawer
        void onOpenMainMenuCallback();

        // for close NavigationDrawer
        void onCloseMainMenuCallback();
    }
}
