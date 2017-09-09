package com.dmelnyk.workinukraine.ui.vacancy_webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 9/8/17.
 */

public class VacancyFragment extends Fragment {

    interface CallbackListener {
        void onExit();
    }

    private static final String ARG_VACANCY = "arg_vacancy";
    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.progress_bar) ProgressBar mBar;
    @BindView(R.id.web_view) WebView mWebView;
//    @BindView(R.id.favorite_image_view) ImageView mFavoriteImageView;
    Unbinder unbinder;

    private String mUrl;
    private String mTitle;
    private CallbackListener mCallback;

    public static VacancyFragment getNewInstance(VacancyModel vacancy) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_VACANCY, vacancy);
        VacancyFragment fragment = new VacancyFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CallbackListener) {
            mCallback = (CallbackListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VacancyModel vacancy = getArguments().getParcelable(ARG_VACANCY);
        mTitle = vacancy.title();
        mUrl = vacancy.url();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_webview, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setting title

        if (savedInstanceState == null) {
            configWebView();
        }

        mTitleTextView.setText(mTitle);
        configProgressBar();
    }

    private void configProgressBar() {
        mBar.setMax(100);
        if (mWebView.getProgress()== 100) {
            mBar.setVisibility(View.GONE);
        }

    }

    private void configWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        // don't show the zoom controls
        mWebView.getSettings().setDisplayZoomControls(false);
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

    @OnClick(R.id.back_image_view) public void onClick() {
        mCallback.onExit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mWebView.stopLoading();
        mWebView.destroy();
        unbinder.unbind();
    }
}
