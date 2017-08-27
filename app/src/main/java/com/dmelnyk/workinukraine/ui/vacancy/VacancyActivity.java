package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.activity_webview.WebViewActivity;
import com.dmelnyk.workinukraine.ui.vacancy.core.BaseTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.SitesTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.core.ScreenSlidePagerAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.ui.vacancy.di.VacancyModule;
import com.dmelnyk.workinukraine.utils.ButtonTabs;
import com.dmelnyk.workinukraine.utils.CustomViewPager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyActivity extends AppCompatActivity implements
        BaseTabFragment.OnFragmentInteractionListener,
        SitesTabFragment.OnFragmentInteractionListener,
        Contract.IVacancyView {

    private static final String KEY_CURRENT_POSITION = "current_position";
    private static final String KEY_TAB_TITLES = "tab_titles";
    private static final String KEY_TAB_VACANCY_COUNT = "tab_vacancy_count";
    private static final String KEY_ORIENTATION_CHANGED = "orientation_changed";
    @Inject
    Contract.IVacancyPresenter presenter;

    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.vacancies_count_text_view) TextView mTitleVacanciesCountTextView;
    @BindView(R.id.pager) CustomViewPager mViewPager;
    @BindView(R.id.button_tabs) ButtonTabs mButtonTabs;
    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;

    private int[] mTabVacancyCount;
    private String[] mTabTitles;
    private Map<String, Map<String, List<VacancyModel>>> mAllVacancies;
    private boolean orientationHasChanged;

    @OnClick(R.id.back_image_view)
    public void onViewClicked() {
        onBackPressed();
    }

    private void initializeDependency(String request) {
        DaggerVacancyComponent.builder()
                .dbModule(new DbModule(getApplicationContext()))
                .vacancyModule(new VacancyModule(request))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy);
        ButterKnife.bind(this);

        mRequest = getIntent().getAction();
        initializeDependency(mRequest);
        initializeViews();
        Log.e("!!!", "request = " + mRequest);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(fab.getRootView(), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        mButtonTabs.setOnTabClickListener(tabClicked -> {
            mViewPager.setCurrentItem(tabClicked);
            updateTitleView(tabClicked);
        });
        mButtonTabs.setSaveEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bindView(this, mRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        onStop();
        presenter.clear();
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.e("444", "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, mViewPager.getCurrentItem());
        outState.putStringArray(KEY_TAB_TITLES, mTabTitles);
        outState.putIntArray(KEY_TAB_VACANCY_COUNT, mTabVacancyCount);
        outState.putBoolean(KEY_ORIENTATION_CHANGED, true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restoring ButtonTab position
        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            mTabTitles = savedInstanceState.getStringArray(KEY_TAB_TITLES);
            mTabVacancyCount = savedInstanceState.getIntArray(KEY_TAB_VACANCY_COUNT);
            orientationHasChanged = savedInstanceState.getBoolean(KEY_ORIENTATION_CHANGED);
            updateTitleView(currentPosition);
        }
    }

    @Override
    public void onFragmentInteractionItemClicked(VacancyModel vacancy, View bodyTextView) {
        Toast.makeText(this, "Vacancy that should be opened = " + vacancy, Toast.LENGTH_SHORT).show();
        Intent webview = WebViewActivity.newInstance(this, vacancy.title(), vacancy.url());
        // run transaction activity
//        ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
//                bodyTextView, getString(R.string.transition_webview_title));

        startActivity(webview/*, option.toBundle()*/);
    }

    @Override
    public void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                      @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        presenter.onItemPopupMenuClicked(vacancy, type);
        Timber.d("TAG", "popup menu clicked");
    }

    @Override
    public void createShareIntent(VacancyModel vacancy) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, vacancy.title() + ": " + vacancy.url());
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public void showResultingMessage(@VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        switch (type) {
            case VacancyCardViewAdapter.MENU_SAVE:
                Toast.makeText(this, R.string.msg_item_saved_to_favorite, Toast.LENGTH_SHORT).show();
                mSlideAdapter.notifyDataSetChanged();
                break;
            case VacancyCardViewAdapter.MENU_REMOVE:
                mSlideAdapter.notifyDataSetChanged();
                Toast.makeText(this, R.string.msg_item_removed_from_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Timber.e("error msg = %s", message);
        Log.e("!!!", message);
        message = getString(R.string.errors_db_favorite_vacancy_already_exists);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayLoadingProcess() {
        Toast.makeText(this, "Loading starts", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoadingProcess() {
        Toast.makeText(this, "Loading finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayTabFragment(
            Map<String, Map<String, List<VacancyModel>>> vacanciesMap) {

        mAllVacancies = new HashMap<>(vacanciesMap);
        mTabVacancyCount = new int[3];

        int siteTabsCount = 0;
        // Counts all vacancies
        for (Map.Entry<String, List<VacancyModel>> vacancyList :
                mAllVacancies.get(IVacancyInteractor.DATA_TAB_SITES).entrySet()) {
            siteTabsCount += vacancyList.getValue().size();
        }

        mTabVacancyCount[0] = siteTabsCount;

        List<VacancyModel> newVacanciesList = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_NEW);

        // Display NEW or RECENT tab
        if (newVacanciesList != null && newVacanciesList.size() != 0) {
            mTabTitles = getResources().getStringArray(R.array.tab_titles_with_new);
            mTabVacancyCount[1] = newVacanciesList.size();
            initializeButtonTabs(true);
        } else {
            mTabTitles = getResources().getStringArray(R.array.tab_titles_with_recent);
            mTabVacancyCount[1] = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS)
                    .get(IVacancyInteractor.VACANCIES_RECENT).size();
            initializeButtonTabs(false);
        }

        mTabVacancyCount[2] = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_FAVORITE).size();

        mSlideAdapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager(), mTabTitles, mAllVacancies);
        mViewPager.setAdapter(mSlideAdapter);

        if (!orientationHasChanged) {
            updateTitleView(0);
        }
    }

    public void initializeButtonTabs(boolean withNewVacanciesTab) {
        int[][] resource = new int[3][2];
        resource[0][0] = R.mipmap.ic_tab_vacancy_light;
        resource[0][1] = R.mipmap.ic_tab_vacancy_dark;

        if (withNewVacanciesTab) {
            resource[1][0] = R.mipmap.ic_tab_new_light;
            resource[1][1] = R.mipmap.ic_tab_new_dark;
        } else {
            resource[1][0] = R.mipmap.ic_tab_recent_light;
            resource[1][1] = R.mipmap.ic_tab_recent_dark;
        }

        resource[2][0] = R.mipmap.ic_tab_favorite_light;
        resource[2][1] = R.mipmap.ic_tab_favorite_dark;

        mButtonTabs.setData(resource);
        mButtonTabs.setVisibility(View.VISIBLE);
    }

    private void updateTitleView(int tabPosition) {
        mTitleTextView.setText(mTabTitles[tabPosition]);
        mTitleVacanciesCountTextView.setText("" + mTabVacancyCount[tabPosition]);
        mButtonTabs.selectTab(tabPosition);
        Log.e("333", "mButtonTabs.selectTab=" + tabPosition);
    }

    @Override
    public void updateFavoriteTab(List<VacancyModel> vacancies) {
        // updating data in adapters
        mSlideAdapter.updateFavoriteData(vacancies);
        // updating title manually if current tab is FAVORITE;
        mTabVacancyCount[2] = vacancies.size();
        if (mViewPager.getCurrentItem() == 2) {
            updateTitleView(2);
        }
    }
}
