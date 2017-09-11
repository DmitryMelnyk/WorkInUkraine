package com.dmelnyk.workinukraine.utils.buttontab;

import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.dmelnyk.workinukraine.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by d264 on 7/27/17.
 */

public class ImageButtonBehavior extends CoordinatorLayout.Behavior<View> {
    Animation animHide;
    Animation animShow;

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        animHide = AnimationUtils.loadAnimation(parent.getContext(), R.anim.fade_out);
        animShow = AnimationUtils.loadAnimation(parent.getContext(), R.anim.fade_in);

        if (dependency instanceof AppBarLayout) {
            updateViewVisibilityForAppBarLayout(parent, (AppBarLayout) dependency, child);
        }
        return false;
    }

    private boolean updateViewVisibilityForAppBarLayout(CoordinatorLayout parent,
                                                        AppBarLayout appBarLayout, View child) {
        Rect appRectangle = new Rect();
        appBarLayout.getGlobalVisibleRect(appRectangle);

        if (appRectangle.height() <= child.getHeight() + 50 && child.getVisibility() == VISIBLE) {
            child.startAnimation(animHide);

        } else if (appRectangle.height() >= child.getHeight() +50 && child.getVisibility() == GONE){
            child.startAnimation(animShow);
        }
        return true;
    }
}
