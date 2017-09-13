package com.dmelnyk.workinukraine.ui.filter;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {

    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.filter_container) ConstraintLayout mContainer;
    @BindView(R.id.text_view) TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupEnterAnimation();
            setupExitAnimation();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupEnterAnimation() {
        Transition transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.change_bound_with_arc);
        transition.setDuration(300);
        getWindow().setSharedElementEnterTransition(transition);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) { /* NOP */ }

            @Override
            public void onTransitionEnd(Transition transition) {
                showMyAwesomeAnimation(mContainer);
            }

            @Override
            public void onTransitionCancel(Transition transition) { /* NOP */ }

            @Override
            public void onTransitionPause(Transition transition) { /* NOP */ }

            @Override
            public void onTransitionResume(Transition transition) { /* NOP */ }
        });
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupExitAnimation() {
        Fade fade = new Fade();
        getWindow().setReturnTransition(fade);
        fade.setDuration(getResources().getInteger(R.integer.animation_duration));
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    // view - animation started from it. In case - fab.
    private void showMyAwesomeAnimation(final View view) {
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        AnimationUtil.animateRevealShow(this, mContainer, mFab.getWidth() / 2, R.color.colorAccent,
                cx, cy, new AnimationUtil.OnRevealAnimationListener() {
                    @Override
                    public void onRevealHide() { /* NOP */ }

                    @Override
                    public void onRevealShow() {
                        // Fading views in after showing this activity animation
                        initViews();
                    }
                });
    }

    // Fade in animation of the layout content
    private void initViews() {
        new Handler(Looper.getMainLooper()).post(() -> {
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            animation.setDuration(getResources().getInteger(R.integer.animation_duration));
            mContainer.startAnimation(animation);
            mContainer.setVisibility(View.VISIBLE);
            mTextView.startAnimation(animation);
            mTextView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimationUtil.animateRevealHide(this, mContainer, mFab, R.color.colorAccent, mFab.getWidth() / 2,
                    new AnimationUtil.OnRevealAnimationListener() {

                        @Override
                        public void onRevealHide() {
                            FilterActivity.this.supportFinishAfterTransition();
                        }

                        @Override
                        public void onRevealShow() { /* NOP */ }
                    });
        } else {
            super.onBackPressed();
        }
    }
}
