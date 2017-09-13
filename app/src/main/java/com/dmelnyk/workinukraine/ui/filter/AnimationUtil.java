package com.dmelnyk.workinukraine.ui.filter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;

import com.dmelnyk.workinukraine.R;

/**
 * Created by d264 on 9/9/17.
 */

class AnimationUtil {

    interface OnRevealAnimationListener {
        void onRevealHide();
        void onRevealShow();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealShow(
            final Context context, final View view, final int startRadius,
            @ColorRes int color, int x, int y, OnRevealAnimationListener listener) {

        float finalRadius = (float) Math.hypot(view.getWidth(), view.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, finalRadius);
        anim.setDuration(context.getResources().getInteger(R.integer.animation_duration));
        anim.setStartDelay(80);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setBackgroundColor(ContextCompat.getColor(context, color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.VISIBLE);
                if (listener != null) {
                    listener.onRevealShow();
                }
                view.findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void animateRevealHide(
            final Context ctx, final View container, final View animationView,
            @ColorRes int color, final int finalRadius, OnRevealAnimationListener listener) {
        int cx = (animationView.getLeft() + animationView.getRight()) / 2;
        int cy = (animationView.getTop() + animationView.getBottom()) / 2;
        int startRadius = container.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(container, cx, cy, startRadius, finalRadius);
        anim.setDuration(ctx.getResources().getInteger(R.integer.animation_duration));
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                container.setBackgroundColor(ContextCompat.getColor(ctx, color));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onRevealHide();
                }

                container.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }
}
