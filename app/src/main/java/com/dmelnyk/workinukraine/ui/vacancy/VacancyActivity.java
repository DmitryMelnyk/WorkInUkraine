package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.dmelnyk.workinukraine.utils.ButtonTabs;

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

    @Inject
    Contract.IVacancyPresenter presenter;

    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.vacancies_count_text_view) TextView mTitleVacanciesCountTextView;
    @BindView(R.id.pager) ViewPager mViewPager;
    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;
    private int[] mTabVacancyCount;
    private String[] mTabTitles;

    @OnClick(R.id.back_image_view)
    public void onViewClicked() {
        onStop();
        onBackPressed();
    }

    private void initializeDependency() {
        DaggerVacancyComponent.builder().dbModule(new DbModule(getApplicationContext()))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        initializeDependency();

        initializeViews();

        mRequest = getIntent().getAction();
        Log.e("!!!", "request = " + mRequest);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        int[][] resource = new int[4][2];
        resource[0][0] = R.mipmap.ic_tab_vacancy_light;
        resource[1][0] = R.mipmap.ic_tab_new_light;
        resource[2][0] = R.mipmap.ic_tab_recent_light;
        resource[3][0] = R.mipmap.ic_tab_favorite_light;

        resource[0][1] = R.mipmap.ic_tab_vacancy_dark;
        resource[1][1] = R.mipmap.ic_tab_new_dark;
        resource[2][1] = R.mipmap.ic_tab_recent_dark;
        resource[3][1] = R.mipmap.ic_tab_favorite_dark;

        ButtonTabs tabs = (ButtonTabs) findViewById(R.id.button_tubs);
        tabs.setData(resource);
        tabs.setOnTabClickListener(tabClicked -> {
            mViewPager.setCurrentItem(tabClicked);
            updateTitleView(tabClicked);
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                Snackbar.make(fab.getRootView(), "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        // Disabling swiping
        mViewPager.setOnTouchListener((view, event) -> true);


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
    public void onFragmentInteractionItemClicked(VacancyModel vacancy) {
        Toast.makeText(this, "Vacancy that should be opened = " + vacancy, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                      @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        Toast.makeText(this, "PopupMenu clicked = " + vacancy + ", " + type, Toast.LENGTH_SHORT).show();
        presenter.onItemPopupMenuClicked(vacancy, type);
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

        mTabTitles = getResources().getStringArray(R.array.tab_titles);
        mTabVacancyCount = new int[4];
        int siteTabsCount = 0;
        for (Map.Entry<String, List<VacancyModel>> vacancyList :
                vacanciesMap.get(IVacancyInteractor.DATA_TAB_SITES).entrySet()) {
            siteTabsCount += vacancyList.getValue().size();
        }
        mTabVacancyCount[0] = siteTabsCount;

        mTabVacancyCount[1] = vacanciesMap.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_NEW).size();

        mTabVacancyCount[2] = vacanciesMap.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_RECENT).size();

        mTabVacancyCount[3] = vacanciesMap.get(IVacancyInteractor.DATA_OTHER_TABS)
                .get(IVacancyInteractor.VACANCIES_FAVORITE).size();


        mSlideAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),
                mTabTitles, vacanciesMap);
        mViewPager.setAdapter(mSlideAdapter);
        updateTitleView(0);
    }

    @Override
    public void updateFavoriteTab(List<VacancyModel> vacancies) {
        mSlideAdapter.updateFavoriteTab(vacancies);
    }
}
