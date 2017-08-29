package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by d264 on 8/20/17.
 */

public class CenteredTabLayout extends TabLayout {
    public CenteredTabLayout(Context context) {
        super(context);
    }

    public CenteredTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup tabLayout = (ViewGroup)getChildAt(0);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        int childCount = getTabCount();
        if (childCount == 0) return;

        int childrenWidth = 0;
        for(int i = 0; i < childCount; i++){
            childrenWidth += tabLayout.getChildAt(i).getMeasuredWidth();
            Log.d("CenteredTabLayout", "childWidth=" + tabLayout.getChildAt(i).getMeasuredWidth());
        }

        if (childrenWidth == screenWidth) return;
        if (childrenWidth < screenWidth) {
            setTabMode(MODE_FIXED);
            setTabGravity(GRAVITY_FILL);
        } else {
            setTabMode(MODE_SCROLLABLE);
        }

        Log.d("CenteredTabLayout", "mode=" + (getTabMode() == 0 ? "MODE_SCROLLABLE" : "MODE_FIXED"));
        Log.d("CenteredTabLayout", "gravity=" + (getTabGravity() == 0 ? "GRAVITY_FILL" : "GRAVITY_CENTER"));
    }
}
