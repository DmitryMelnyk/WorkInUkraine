package com.dmelnyk.workinukraine.ui.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.dmelnyk.workinukraine.R;


/**
 * Created by d264 on 6/10/17.
 */

public class NavUtil {

    Context mContext;

    public NavUtil(Context context) {
        mContext = context;
    }

    public String[] getNavTitles() {
        return mContext.getResources().getStringArray(R.array.nav_titles);
    }

    public Drawable[] getNavIcons() {
        TypedArray array = mContext.getResources().obtainTypedArray(R.array.nav_titles_icon);
        Drawable[] icons = new Drawable[array.length()];
        for (int i = 0; i < array.length(); i++) {
            int id = array.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(mContext, id);
            }
        }
        array.recycle();
        return icons;
    }

}
