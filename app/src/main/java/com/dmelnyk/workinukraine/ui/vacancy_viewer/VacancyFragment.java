package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 9/8/17.
 */

// TODO: error downloading page: see screenshot in mobile
public class VacancyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String ABOUT_BLANK = "about:blank";
    private Drawable mIcon;

    private String mDownloadedTitle;

    private VacancyModel mVacancy;

    private static final String ARG_VACANCY = "arg_vacancy";

    @BindView(R.id.swipe) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.progress_bar) ProgressBar mBar;
    @BindView(R.id.web_view) WebView mWebView;
    Unbinder unbinder;

    private String mTitle;
    private String mUrl;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vacancy, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Configuring progress bar / restoring state
        mSwipeRefreshLayout.setOnRefreshListener(this);
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

    private void configWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        // don't show the zoom controls
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(getWebViewClient());


        mWebView.setWebViewClient(new CustomWebViewClient());

//        if (isConnected()) {
            mWebView.loadUrl(mUrl);
//        }
//        checkInetStatus(null);
    }

    private WebChromeClient getWebViewClient() {
        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (mBar == null) return;

                mBar.setVisibility(View.VISIBLE);
                mBar.setProgress(newProgress);
                if (newProgress == 100) {
                    mBar.setVisibility(View.GONE);
                    // stop refreshing
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (isAdded()) {
                    if (mDownloadedTitle == null || !mDownloadedTitle.equals(ABOUT_BLANK)) {
                        mDownloadedTitle = title;
                        mTitle = mDownloadedTitle;
                        updateParentTitle(mTitle);
                    }
                }
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                super.onReceivedTouchIconUrl(view, url, precomposed);
                if (isAdded()) {
                    Glide.with(getContext())
                            .load(url)
                            .into(new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                                    mIcon = resource;
                                    updateParentIcon(resource);
                                }
                            });

                }
            }
        };
    }

    private void updateParentTitle(String mTitle) {
        if (mCallback != null) {
            mCallback.updateTitle(mVacancy, mTitle);
        }
    }

    private void updateParentIcon(Drawable icon) {
        if (mCallback != null) {
            mCallback.updateSiteIcon(mVacancy, icon);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    /**
     * WebViewClient subclass loads all hyperlinks in the existing WebView
     */
    private class CustomWebViewClient extends WebViewClient {

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // When user clicks a hyperlink, load in the existing WebView
            if (url.startsWith("http"))
                return false;
            else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                startActivity(i);
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (isAdded()) {
                // TODO
//                if (isConnected()) {
//                    mNoConnectionTextView.setVisibility(View.VISIBLE);
//                } else {
//                    mNoConnectionTextView.setVisibility(View.GONE);
//                }
                Toast.makeText(getContext(), "Page downloaded", Toast.LENGTH_SHORT).show();
            }

            Log.d(VacancyFragment.this.getClass().getSimpleName(), "onPageFinished()");
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request,
                                    WebResourceError error) {

            Log.d(VacancyFragment.this.getClass().getSimpleName(), "onReceivedError=" + error.getErrorCode());
            // TODO
//            checkInetStatus(null);
            super.onReceivedError(view, request, error);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view,
                                        WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.d(VacancyFragment.this.getClass().getSimpleName(), "onReceivedError=" + errorResponse.getStatusCode());
            // TODO
//            checkInetStatus(errorResponse);
            super.onReceivedHttpError(view, request, errorResponse);
        }
    }



    @Override
    public void onRefresh() {
        Log.d(getClass().getSimpleName(), "SwipeRefreshLayout.onRefresh()");

        Toast.makeText(getContext(), "Swipe!!!!", Toast.LENGTH_SHORT).show();
        if (isConnected()) {
//            showNoConnectionView(false);
            mWebView.loadUrl(ABOUT_BLANK);
            mWebView.loadUrl(mUrl);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
//            checkInetStatus(null);
        }
    }

    private boolean isConnected() {
        // todo
        return true;
//        return mCallback.isConnected();
    }


    interface CallbackListener {
        void updateFavoriteVacancy(VacancyModel vacancy);

        void updateTitle(VacancyModel vacancy, String mTitle);

        void updateSiteIcon(VacancyModel vacancy, Drawable icon);

        boolean isConnected();
    }
}
