package com.dmelnyk.workinukraine.mvp.activity_webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.Job;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dmitry on 22.01.17.
 */

public class WebViewActivity extends AppCompatActivity implements Contract.View{

    public static final String ARGS = "WebViewActivity.Args";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_vacancy_progress_bar) ProgressBar bar;
    @BindView(R.id.web_view) WebView mWebView;

    private String url;
    private String title;
    private WebActivityPresenter presenter;
    private Job job;
    private int menuView = -1; // for changing menu


    public static Intent newInstance(Context context, Job job) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ARGS, job);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_fragment);
        ButterKnife.bind(this);

        job = getIntent().getParcelableExtra(ARGS);
        url = job.getUrlCode();
        title = job.getTitle();

        initializePresenter();
        presenter.onTakeView(this);

        configToolbar();
        configProgressBar();
        if (savedInstanceState == null) {
            configWebView();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onTakeView(null);
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new WebActivityPresenter(this, job);
        }
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(
                view -> finish());
    }

    public void onChangeMenu(int id) {
        menuView = id;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuView, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.webview_menu_favorite:
                presenter.onMenuItemSelected();
                break;
        }
        return true;
    }

    private void configProgressBar() {
        bar.setMax(100);
    }

    private void configWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                bar.setProgress(newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http"))
                    return false;
                else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    startActivity(i);
                    return true;
                }
            }
        });
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        boolean canGoBack = mWebView.canGoBack();
        if (canGoBack) {
            mWebView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
