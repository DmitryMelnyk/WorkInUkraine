package com.dmelnyk.workinukraine.ui.vacancy.core;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.lang.reflect.Field;

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

        int childCount = tabLayout.getChildCount();

        int widths[] = new int[childCount];
        int globalChildWidth = 0;
        for(int i = 0; i < childCount; i++){
            globalChildWidth += widths[i] = tabLayout.getChildAt(i).getMeasuredWidth();
        }

        int measuredWidth = getMeasuredWidth();
        int freeSpace = measuredWidth - globalChildWidth;
        if (freeSpace < 0) return;

        int additionWidth = freeSpace / childCount;
        for(int i = 0; i < childCount; i++){
            tabLayout.getChildAt(i).setMinimumWidth(widths[i] + additionWidth);
        }

    }
}
