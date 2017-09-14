package com.dmelnyk.workinukraine.ui.vacancy_viewer;

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
import android.widget.ImageView;
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

    @BindView(R.id.favorite_image_view)
    ImageView mFavoriteImageView;

    interface CallbackListener {
        void onExit();

        void updateFavoriteVacancy(VacancyModel vacancy);
    }

    private VacancyModel mVacancy;

    private static final String ARG_VACANCY = "arg_vacancy";
    @BindView(R.id.title_text_view)
    TextView mTitleTextView;
    @BindView(R.id.progress_bar)
    ProgressBar mBar;
    @BindView(R.id.web_view)
    WebView mWebView;
    @BindView(R.id.menu_image_view)
    ImageView mMenuImageView;

    Unbinder unbinder;

    private String mUrl;
    private String mTitle;
    private boolean mIsFavorite;
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
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVacancy = getArguments().getParcelable(ARG_VACANCY);
        mTitle = mVacancy.title();
        mUrl = mVacancy.url();
        mIsFavorite = mVacancy.isFavorite();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            configWebView();
        }

        // Setting title
        mTitleTextView.setText(mTitle);
        // Configuring progress bar / restoring state
        configProgressBar();
        configFavoriteIcon();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void configProgressBar() {
        mBar.setMax(100);
        if (mWebView.getProgress() == 100) {
            mBar.setVisibility(View.GONE);
        }

    }

    private void configFavoriteIcon() {
        int imageResource = mIsFavorite
                ? R.drawable.ic_favorite_green
                : R.drawable.ic_favorite_white;

        mFavoriteImageView.setImageResource(imageResource);
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

    @OnClick({R.id.favorite_image_view, R.id.menu_image_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorite_image_view:
                mCallback.updateFavoriteVacancy(mVacancy);
                mIsFavorite = !mIsFavorite;
                configFavoriteIcon();
                break;

            case R.id.menu_image_view:
                // TODO
        }
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

}
