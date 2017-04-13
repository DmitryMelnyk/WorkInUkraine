package com.dmelnyk.workinukraine.helpers;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.dmelnyk.workinukraine.R;

/**
 * Created by dmitry on 16.03.17.
 */

public class ScrollingFABBehaviour extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private int toolbarHeight;

    public ScrollingFABBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = (int) context.getTheme()
                .obtainStyledAttributes(new int[]{R.attr.actionBarSize})
                .getDimension(0, 0);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ration = (float) dependency.getY()/(float)toolbarHeight;
            fab.setTranslationX(-distanceToScroll * ration);
        }

        return true;
    }
}
