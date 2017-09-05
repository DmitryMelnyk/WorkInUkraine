package com.dmelnyk.workinukraine.ui.vacancy_webview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by dmitry on 22.01.17.
 */

public class WebViewActivity extends BaseAnimationActivity {

    public static final String EXTRA_VACANCY = "extra_vacancy";
    private static final String EXTRA_IS_FAVORITE = "extra_is_favorite";

    public static final int RESULT_REMOVE_FROM_FAVORITES = 2001;
    public static final int RESULT_ADD_TO_FAVORITES = 2002;

    @BindView(R.id.title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.progress_bar)
    ProgressBar mBar;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.favorite_image_view)
    ImageView mFavoriteImageView;

    private String mUrl;
    private boolean mIsVacancyFavorite;

    public static Intent newInstance(Context context, VacancyModel vacancy, boolean isVacancyFavorite) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_VACANCY, vacancy);
        intent.putExtra(EXTRA_IS_FAVORITE, isVacancyFavorite);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        VacancyModel vacancy = getIntent().getParcelableExtra(EXTRA_VACANCY);
        mIsVacancyFavorite = getIntent().getBooleanExtra(EXTRA_IS_FAVORITE, false);

        String title = vacancy.title();
        mUrl = vacancy.url();

        configProgressBar();
        mTitleTextView.setText(title);

        findViewById(R.id.back_image_view).setOnClickListener(view -> onBackPressed());
        changeFavoriteImage();

        mFavoriteImageView.setOnClickListener(view -> {
            mIsVacancyFavorite = !mIsVacancyFavorite;

            changeFavoriteImage();
            int resultCode = mIsVacancyFavorite
                    ? RESULT_ADD_TO_FAVORITES
                    : RESULT_REMOVE_FROM_FAVORITES;

            Intent result = new Intent();
            result.putExtra(EXTRA_VACANCY, vacancy);
            setResult(resultCode, result);
        });

        if (savedInstanceState == null) {
            configWebView();
        }
    }

    private void changeFavoriteImage() {
        mFavoriteImageView.setImageDrawable(mIsVacancyFavorite
                ? ContextCompat.getDrawable(this, R.mipmap.ic_star_green)
                : ContextCompat.getDrawable(this, R.mipmap.ic_star_white)
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

    private void configProgressBar() {
        mBar.setMax(100);
    }

    private void configWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(getWebViewClient());

        mWebView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
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
        } else {
            finish();
        }
    }

    public WebChromeClient getWebViewClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTitleTextView.setText(title);
            }
        };
    }

    private boolean isConnected;

    private void checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = false;
        if (activeNetwork != null && activeNetwork.isConnected()) {
            isConnected = true;


        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
            builder.setTitle("Connection failed");
            builder.setMessage("The application without the internet connection may not work. Please check your internet connection.");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    checkConnection();
                }
            });
            builder.show();

        }
    }
}
