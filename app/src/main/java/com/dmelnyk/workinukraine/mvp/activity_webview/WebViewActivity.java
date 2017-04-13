package com.dmelnyk.workinukraine.mvp.activity_webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dmelnyk.workinukraine.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dmitry on 22.01.17.
 */

public class WebViewActivity extends AppCompatActivity {

    public static final String ARGS_URL_ADDRESS = "WebViewActivity.URL";
    public static final String ARGS_TITLE = "WebViewActivity.TITLE";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_vacancy_progress_bar) ProgressBar bar;
    @BindView(R.id.web_view) WebView mWebView;

    private String mUrl;
    private String title;

    public static Intent newInstance(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(ARGS_URL_ADDRESS, url);
        intent.putExtra(ARGS_TITLE, title);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_fragment);
        ButterKnife.bind(this);

        Intent args = getIntent();
        mUrl = args.getStringExtra(ARGS_URL_ADDRESS);
        title = args.getStringExtra(ARGS_TITLE);

        configToolbar();
        configProgressBar();
        configWebView();
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
        toolbar.setNavigationOnClickListener(
                view -> finish());
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
        mWebView.loadUrl(mUrl);
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
}
