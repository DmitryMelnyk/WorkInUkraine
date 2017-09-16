package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 9/8/17.
 */

// TODO: error downloading page: see screenshot in mobile
public class VacancyFragment extends Fragment {

    interface CallbackListener {

        void onExit();
        void updateFavoriteVacancy(VacancyModel vacancy);
    }
    private VacancyModel mVacancy;

    private static final String ARG_VACANCY = "arg_vacancy";

    @BindView(R.id.site_icon_image_view) ImageView mSiteIconImageView;
    @BindView(R.id.favorite_image_view) ImageView mFavoriteImageView;
    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.date_text_view) TextView mDateTextView;
    @BindView(R.id.progress_bar) ProgressBar mBar;
    @BindView(R.id.web_view) WebView mWebView;

    Unbinder unbinder;

    private String mDate;
    private String mTitle;
    private String mUrl;
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
        mDate = mVacancy.date();
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
        // Setting title and date
        mTitleTextView.setText(mTitle);
        mDateTextView.setText(mDate);
        configFavoriteIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Configuring progress bar / restoring state
        configProgressBar();
        configWebView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void configProgressBar() {
        mBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);
        mBar.setMax(100);
        if (mWebView.getProgress() == 100) {
            mBar.setVisibility(View.GONE);
        }

    }

    private void configFavoriteIcon() {
        int imageResource = mIsFavorite
                ? R.drawable.ic_favorite_green
                : R.drawable.vacancy_favorite_blue;

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
                if (mBar == null) return;

                mBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (isAdded()) {
                    mTitleTextView.setText(title);
                }
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
                if (isAdded()) {
                    Glide.with(getContext()).load(url).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (isAdded()) {
                                mSiteIconImageView.setVisibility(View.VISIBLE);
                            }
                            return false;
                        }
                    }).into(mSiteIconImageView);
                }
            }
        };
    }

    @OnClick({R.id.back_image_view, R.id.favorite_image_view, R.id.share_image_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_image_view:
                mCallback.onExit();
                break;

            case R.id.favorite_image_view:
                mCallback.updateFavoriteVacancy(mVacancy);
                mIsFavorite = !mIsFavorite;
                configFavoriteIcon();
                break;

            case R.id.share_image_view:
                createShareIntent(mVacancy);
                break;
        }
    }

    public void createShareIntent(VacancyModel vacancy) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, vacancy.title() + ": " + vacancy.url());
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        mWebView.saveState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
//        mWebView.restoreState(savedInstanceState);
    }

}
