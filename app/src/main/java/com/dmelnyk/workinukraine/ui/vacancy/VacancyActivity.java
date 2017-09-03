package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.vacancy_webview.WebViewActivity;
import com.dmelnyk.workinukraine.ui.vacancy.core.BaseTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.SitesTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy.core.VacancyCardViewAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.core.ScreenSlidePagerAdapter;
import com.dmelnyk.workinukraine.ui.vacancy.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.ui.vacancy.di.VacancyModule;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;
import com.dmelnyk.workinukraine.utils.buttontab.ButtonTabs;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyActivity extends BaseAnimationActivity implements
        BaseTabFragment.OnFragmentInteractionListener,
        SitesTabFragment.OnFragmentInteractionListener,
        Contract.IVacancyView {

    private static final String KEY_CURRENT_POSITION = "current_position";
    private static final String KEY_TAB_TITLES = "tab_titles";
    private static final String KEY_TAB_VACANCY_COUNT = "tab_vacancy_count";
    private static final String KEY_ORIENTATION_CHANGED = "orientation_changed";
    private static final int WEBVIEW_REQUEST_CODE = 1002;

    @Inject Contract.IVacancyPresenter presenter;

    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.vacancies_count_text_view) TextView mTitleVacanciesCountTextView;
    @BindView(R.id.pager) ViewPager mViewPager;
    @BindView(R.id.button_tabs) ButtonTabs mButtonTabs;
    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;

    private int[] mTabVacancyCount;
    private String[] mTabTitles;
    private boolean orientationHasChanged;

    @OnClick(R.id.back_image_view)
    public void onViewClicked() {
        onBackPressed();
    }

    private void initializeDependency(String request) {
        DaggerVacancyComponent.builder()
                .dbModule(new DbModule(getApplicationContext()))
                .vacancyModule(new VacancyModule(request, getApplicationContext()))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy);
        ButterKnife.bind(this);

        setResult(RESULT_OK);
        // Makes status bar transparent
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        mRequest = getIntent().getAction();
        initializeDependency(mRequest);
        initializeViews();
        presenter.bindView(this, mRequest);
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
        presenter.bindJustView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        presenter.clear();
        finish();
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
        boolean isVacancyFavorite = presenter.isVacancyFavorite(vacancy);
        Intent webview = WebViewActivity.newInstance(this, vacancy, isVacancyFavorite);
        startActivityForResult(webview, WEBVIEW_REQUEST_CODE);
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
    public void displayTabFragment(
            String[] tabTitles,
            int[] tabVacancyCount,
            boolean isButtonTubWithNewIcon,
            Map<String, Map<String, List<VacancyModel>>> allVacancies) {

        // initialize ButtonTubs
        initializeButtonTabs(isButtonTubWithNewIcon);
        // Initialize tab titles
        mTabTitles = tabTitles;
        mTabVacancyCount = tabVacancyCount;
        mSlideAdapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager(), mTabTitles, allVacancies);
        mViewPager.setAdapter(mSlideAdapter);

        if (!orientationHasChanged) {
            updateTitleView(0);
        }
    }

    @Override
    public void exitActivity() {
        setResult(RESULT_CANCELED);
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("888", "onActivityResult in VacancyActivity");
        if (requestCode == WEBVIEW_REQUEST_CODE) {
            switch (resultCode) {
                case WebViewActivity.RESULT_ADD_TO_FAVORITES:
                    presenter.onItemPopupMenuClicked(
                            (VacancyModel) data.getParcelableExtra(WebViewActivity.EXTRA_VACANCY),
                            VacancyCardViewAdapter.MENU_SAVE
                    );
                    break;
                case WebViewActivity.RESULT_REMOVE_FROM_FAVORITES:
                    presenter.onItemPopupMenuClicked(
                            (VacancyModel) data.getParcelableExtra(WebViewActivity.EXTRA_VACANCY),
                            VacancyCardViewAdapter.MENU_REMOVE
                    );
                    break;
                default:
                    /* NOP */
            }
        }
    }
}
