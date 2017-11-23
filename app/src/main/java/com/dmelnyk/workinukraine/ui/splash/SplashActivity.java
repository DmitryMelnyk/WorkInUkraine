package com.dmelnyk.workinukraine.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;
import com.hanks.htextview.HTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

//    @BindView(R.id.htex_view)
//    HTextView hTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_splash);
//
//        ButterKnife.bind(this);
//
//        hTextView.postDelayed(() -> hTextView.animateText("Work In Ukraine"), 500);
//        hTextView.postDelayed(() -> {
//            finish();
//        }, 2500);
        getWindow().getDecorView().postDelayed(() -> {
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
            finish();
        }, 500);
    }
}
