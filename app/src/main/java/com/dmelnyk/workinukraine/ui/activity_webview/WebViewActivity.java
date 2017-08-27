package com.dmelnyk.workinukraine.ui.activity_webview;

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
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dmitry on 22.01.17.
 */

public class WebViewActivity extends AppCompatActivity implements Contract.View{

    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_URL = "extra_url";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_vacancy_progress_bar) ProgressBar bar;
    @BindView(R.id.web_view) WebView mWebView;

    private String mUrl;
    private String mTitle;
    private WebActivityPresenter presenter;
    private int menuView = -1; // for changing menu


    public static Intent newInstance(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
//
        mUrl = getIntent().getStringExtra(EXTRA_URL);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

//        initializePresenter();
//        presenter.onTakeView(this);

//        configToolbar();
        configProgressBar();
        TextView titleTextView = (TextView) findViewById(R.id.title_text_view);
        titleTextView.setText(mTitle);

        findViewById(R.id.back_image_view).setOnClickListener(view -> onBackPressed());

        if (savedInstanceState == null) {
            configWebView();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
//        presenter.onTakeView(null);
//        presenter = null;
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
//            presenter = new WebActivityPresenter(this, job);
        }
    }

//    private void configToolbar() {
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(mTitle);
//        toolbar.setNavigationOnClickListener(
//                view -> finish());
//    }

    public void onChangeMenu(int id) {
        menuView = id;
        invalidateOptionsMenu();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(menuView, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.webview_menu_favorite:
//                presenter.onMenuItemSelected();
//                break;
//        }
//        return true;
//    }

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
            finish(); //supportFinishAfterTransition();
        }
    }

    @Override
    public void onShowToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
