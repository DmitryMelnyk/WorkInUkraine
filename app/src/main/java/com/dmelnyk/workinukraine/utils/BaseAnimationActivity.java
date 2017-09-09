package com.dmelnyk.workinukraine.utils;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.dmelnyk.workinukraine.R;

/**
 * Created by d264 on 9/3/17.
 */

public class BaseAnimationActivity extends AppCompatActivity {

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransitionEnter();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    public void onExit() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation
     */
    private void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation
     */
    private void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
