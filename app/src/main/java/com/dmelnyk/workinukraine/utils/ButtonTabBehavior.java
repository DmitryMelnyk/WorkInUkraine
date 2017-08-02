package com.dmelnyk.workinukraine.utils;

import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by d264 on 7/27/17.
 */

public class ButtonTabBehavior extends CoordinatorLayout.Behavior<ButtonTabs> {
    public static final String TAG = "TAG!!!";
    private static final boolean AUTO_HIDE_DEFAULT = true;
    private boolean mAutoHideEnabled = true;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ButtonTabs child, View dependency) {
        if (dependency instanceof AppBarLayout) {
//            Log.d(TAG, "onDependentViewChanged()");
            // If we're depending on an AppBarLayout we will show/hide it automatically
            // if the FAB is anchored to the AppBarLayout
            updateViewVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }

    private boolean updateViewVisibilityForAppBarLayout(CoordinatorLayout parent,
                                                        AppBarLayout appBarLayout, ButtonTabs child) {
        if (!shouldUpdateVisibility(appBarLayout, child)) {
            return false;
        }

        Rect appRectangle = new Rect();
        appBarLayout.getGlobalVisibleRect(appRectangle);
//        Log.d(TAG, "appBarLayout.getMinimumHeight = " + appRectangle.height());

        if (appRectangle.height() <= child.getHeight() + 50 && child.getVisibility() == VISIBLE) {
            // If the anchor's bottom is below the seam, we'll animate our FAB out
            child.animHide();

        } else if (appRectangle.height() >= child.getHeight() +50 && child.getVisibility() == GONE){
            // Else, we'll animate our FAB back in
            child.animShow();
        }
        return true;
    }

    private boolean shouldUpdateVisibility(View dependency, ButtonTabs child) {
        final CoordinatorLayout.LayoutParams lp =
                (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        if (!mAutoHideEnabled) {
            return false;
        }

        if (lp.getAnchorId() != dependency.getId()) {
            // The anchor ID doesn't match the dependency, so we won't automatically
            // show/hide the ButtonTub
            return false;
        }

        return true;
    }
}
