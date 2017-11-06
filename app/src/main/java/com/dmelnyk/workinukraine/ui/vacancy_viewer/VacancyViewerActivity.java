package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceResponse;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.di.DaggerVacancyViewerComponent;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyViewerActivity extends BaseAnimationActivity
    implements Contract.IVacancyViewerView, VacancyFragment.CallbackListener {

    public static final String DATA_FAVORITE = "favorite";
    public static final String DATA_NEW = "new";
    public static final String DATA_RECENT = "recent";
    public static final String DATA_SITE = "site";

    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_VACANCY_TO_DISPLAY = "extra_display_vacancy";

    @BindView(R.id.vacancy_container) ViewPager mVacancyContainer;
    @BindView(R.id.site_icon_image_view) ImageView mSiteIconImageView;
    @BindView(R.id.favorite_image_view) ImageView mFavoriteImageView;
    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.date_text_view) TextView mDateTextView;
    @BindView(R.id.tv_no_inet_connection) TextView mNoConnectionTextView;
    Unbinder unbinder;

    @Inject
    Contract.IVacancyViewerPresenter presenter;
    List<VacancyModel> mVacancies;
    private VacancyAdapter mAdapter;

    private String mRequest;
    private String mSite;
    private String mType;
    private VacancyModel mVacancyToDisplay;

    public static Intent getIntent(
            Context context,
            VacancyModel vacancyToOpen,
            String type) {
        Intent intent = new Intent(context, VacancyViewerActivity.class);
        intent.putExtra(EXTRA_VACANCY_TO_DISPLAY, vacancyToOpen);
        intent.putExtra(EXTRA_TYPE, type);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_container);
        unbinder = ButterKnife.bind(this);

        // Inject dependency
        DaggerVacancyViewerComponent.builder()
                .dbModule(new DbModule(getApplicationContext()))
                .build().inject(this);

        // gets vacancy to show position
        mVacancyToDisplay = getIntent().getParcelableExtra(EXTRA_VACANCY_TO_DISPLAY);
        mRequest = mVacancyToDisplay.request();
        mSite = mVacancyToDisplay.site();
        mType = getIntent().getStringExtra(EXTRA_TYPE);

//        ((NestedScrollView) findViewById(R.id.nsv)).setFillViewport (true);
        presenter.bindView(this);
        presenter.getData(mRequest, mType, mSite);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void displayVacancies(List<VacancyModel> vacancies) {
        mVacancies = vacancies;
        mAdapter = new VacancyAdapter(getSupportFragmentManager(), mVacancies);
        mVacancyContainer.setAdapter(mAdapter);
        mVacancyContainer.setCurrentItem(findPosition(mVacancyToDisplay, mVacancies));
        mVacancyContainer.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        updateToolbar(position);
                    }
                });
    }

    private void updateToolbar(int position) {
        VacancyFragment currentFragment = (VacancyFragment) mAdapter.getItem(position);
        String title = currentFragment.getTitle();
        Drawable icon = currentFragment.getIcon();
        String date = mVacancies.get(position).date();

        // setting data to views
        mDateTextView.setText(date);
        mTitleTextView.setText(title);

        configFavoriteIcon();

        if (icon != null) {
            mSiteIconImageView.setVisibility(View.VISIBLE);
            mSiteIconImageView.setImageDrawable(icon);
        }
    }

    private int findPosition(VacancyModel vacancyToDisplay, List<VacancyModel> vacancies) {
        for (int i = 0; i < vacancies.size(); i++) {
            if (vacancies.get(i).equals(vacancyToDisplay)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onExit() {
        onBackPressed();
    }

    @Override
    public void updateFavoriteVacancy(VacancyModel vacancy) {
        presenter.updateFavoriteStatusVacancy(vacancy);
        VacancyModel updatedVacancy = vacancy.getUpdatedFavoriteVacancy();
        Timber.i("updatedVacancy=" + updatedVacancy);
        mVacancies.set(mVacancies.indexOf(vacancy), updatedVacancy);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

    @Override
    public void showUpdatingVacancySuccess(Boolean isFavorite) {
        String msg = isFavorite
                ? getString(R.string.msg_item_saved_to_favorite)
                : getString(R.string.msg_item_removed_from_favorite);

        Snackbar.make(mVacancyContainer, msg, 2000).show();
    }

    @Override
    public void showUpdatingVacancyError() {
        // TODO: move to strings
        Toast.makeText(this, R.string.msg_vacancy_update_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void updateTitle(VacancyModel vacancy, String title) {
        if (mVacancies.get(mVacancyContainer.getCurrentItem()).equals(vacancy)) {
            mTitleTextView.setText(title);
        }
    }

    @Override
    public void updateSiteIcon(VacancyModel vacancy, Drawable icon) {
        if (mVacancies.get(mVacancyContainer.getCurrentItem()).equals(vacancy)) {
            mSiteIconImageView.setVisibility(View.VISIBLE);
            mSiteIconImageView.setImageDrawable(icon);
        }
    }

    @OnClick({R.id.back_image_view, R.id.favorite_image_view, R.id.share_image_view})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_image_view:
                finish();
                break;

            case R.id.favorite_image_view:
                updateFavoriteVacancy(mVacancies.get(mVacancyContainer.getCurrentItem()));
//                mIsFavorite = !mIsFavorite;
                configFavoriteIcon();
                break;

            case R.id.share_image_view:
                createShareIntent(mVacancies.get(mVacancyContainer.getCurrentItem()));
                break;
        }
    }

    private void configFavoriteIcon() {
        boolean isFavorite = mVacancies.get(mVacancyContainer.getCurrentItem()).isFavorite();
        int imageResource = isFavorite
                ? R.drawable.ic_favorite_green
                : R.drawable.vacancy_favorite_blue;
        mFavoriteImageView.setImageResource(imageResource);
    }

    public void createShareIntent(VacancyModel vacancy) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, vacancy.title() + ": " + vacancy.url());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private void showNoConnectionView(boolean show) {
        if (show) {
            mNoConnectionTextView.setVisibility(View.VISIBLE);
        } else {
            mNoConnectionTextView.setVisibility(View.GONE);
        }
    }

    private void checkInetStatus(WebResourceResponse errorResponse) {
        if (isConnected()) {
            // display web content
            showNoConnectionView(false);
        } else {
            final Snackbar snackBar = Snackbar.make(mTitleTextView, R.string.msg_no_inet_connection_short, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction("Enable Data", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
                    // TODO
//                    mWebView.loadUrl("javascript:window.location.reload(true)");
                    snackBar.dismiss();
                }
            });
            Toast.makeText(this, R.string.msg_no_inet_connection_long, Toast.LENGTH_SHORT).show();
            snackBar.show();
            showNoConnectionView(true);
        }
    }

    /**
     * Check if there is any connectivity
     *
     * @return is Device Connected
     */
    public boolean isConnected() {
        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            isConnected = (info != null && info.isConnected() && info.isAvailable());
        }

        Log.d(getClass().getSimpleName(), "isConnected=" + isConnected);
        return isConnected;
    }
}
