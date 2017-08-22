package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.dmelnyk.workinukraine.ui.vacancy.core.BaseTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.SitesTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.core.ScreenSlidePagerAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.ui.vacancy.di.VacancyModule;
import com.dmelnyk.workinukraine.utils.ButtonTabs;
import com.dmelnyk.workinukraine.utils.CustomViewPager;

import java.util.ArrayList;
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
    @Inject
    Contract.IVacancyPresenter presenter;

    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.vacancies_count_text_view) TextView mTitleVacanciesCountTextView;
    @BindView(R.id.pager) CustomViewPager mViewPager;
    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;
    private int[] mTabVacancyCount;
    private String[] mTabTitles;
    private Map<String, Map<String, List<VacancyModel>>> mAllVacancies;

    @OnClick(R.id.back_image_view)
    public void onViewClicked() {
        onStop();
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
        setContentView(R.layout.activity_scrolling);
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
    }

    private void updateTitleView(int tabPosition) {
        mTitleTextView.setText(mTabTitles[tabPosition]);
        mTitleVacanciesCountTextView.setText("" + mTabVacancyCount[tabPosition]);
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
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.clear();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restoring ButtonTab position
        if (savedInstanceState != null && mTabTitles != null) {
            int currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            updateTitleView(currentPosition);
        }
    }

    @Override
    public void onFragmentInteractionItemClicked(VacancyModel vacancy) {
        Toast.makeText(this, "Vacancy that should be opened = " + vacancy, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                      @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        presenter.onItemPopupMenuClicked(vacancy, type);
        Timber.d("TAG", "popup menu clicked");
    }

    @Override
    public void openVacancyInWeb(String url) {
        Toast.makeText(this, "openVacancyInWeb", Toast.LENGTH_SHORT).show();
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
            initializeButtonTans(true);
        } else {
            mTabTitles = getResources().getStringArray(R.array.tab_titles_with_recent);
            mTabVacancyCount[1] = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS)
                    .get(IVacancyInteractor.VACANCIES_RECENT).size();
            initializeButtonTans(false);
        }

        mTabVacancyCount[2] = mAllVacancies.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_FAVORITE).size();

        mSlideAdapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager(), mTabTitles, mAllVacancies);
        mViewPager.setAdapter(mSlideAdapter);

        // show NEW tab if there are new vacancies
        if (newVacanciesList.size() != 0) {
            updateTitleView(1);
            mViewPager.setCurrentItem(1);
        } else {
            updateTitleView(0);
        }
    }

    public void initializeButtonTans(boolean withNewVacanciesTab) {
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

        ButtonTabs buttonTabs = (ButtonTabs) findViewById(R.id.button_tubs);
        buttonTabs.setData(resource);
        buttonTabs.setVisibility(View.VISIBLE);
        buttonTabs.setOnTabClickListener(tabClicked -> {
            mViewPager.setCurrentItem(tabClicked);
            updateTitleView(tabClicked);
        });

        if (withNewVacanciesTab) {
            // selecting NEW tab position
            buttonTabs.selectTab(1);
        }
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
