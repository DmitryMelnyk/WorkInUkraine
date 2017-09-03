package com.dmelnyk.workinukraine.utils.buttontab;

import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by d264 on 7/27/17.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ButtonTabOutline extends ViewOutlineProvider {
    private final int mWidth;
    private final int mHeight;

    public ButtonTabOutline(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRoundRect(0, 0, mWidth, mHeight, mHeight / 2);
    }
}
