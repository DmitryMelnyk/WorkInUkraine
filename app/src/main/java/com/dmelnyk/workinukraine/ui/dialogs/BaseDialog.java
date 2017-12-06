package com.dmelnyk.workinukraine.ui.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Window;

import com.dmelnyk.workinukraine.R;

/**
 * Created by dmitry on 03.04.17.
 */

public abstract class BaseDialog extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    // View-to-point reduction animation
//    @Override
//    public void dismiss() {
//        getView().animate()
//                .scaleX(0)
//                .scaleY(0)
//                .setInterpolator(new AccelerateInterpolator(2));
//
//        getView().postDelayed(() -> super.dismiss(), 300);
//    }
}
