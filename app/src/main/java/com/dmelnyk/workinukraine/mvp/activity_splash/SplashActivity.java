package com.dmelnyk.workinukraine.mvp.activity_splash;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.dmelnyk.workinukraine.R;
import com.hanks.htextview.HTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity
    implements Contract.View {

    @BindView(R.id.htex_view)
    HTextView hTextView;

    SplashActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        initializePresenter();
        presenter.onTakeView(this);

        hTextView.postDelayed(() -> hTextView.animateText("Work In Ukraine"), 500);
        hTextView.postDelayed(() -> {
            // run new Activity
            presenter.onSplashClosed();
            finish();
        }, 2500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onTakeView(null);
        presenter = null;
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new SplashActivityPresenter(this);
        }
    }

    @Override
    public void onBackPressed() {
        // NOP
    }
}
