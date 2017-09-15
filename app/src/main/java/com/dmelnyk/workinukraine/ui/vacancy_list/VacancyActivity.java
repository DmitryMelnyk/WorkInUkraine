package com.dmelnyk.workinukraine.ui.vacancy_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.business.vacancy_list.IVacancyListInteractor;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.filter.FilterActivity;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerActivity;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.WebViewActivity;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.BaseTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.SitesTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.ScreenSlidePagerAdapter;
import com.dmelnyk.workinukraine.ui.vacancy_list.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.ui.vacancy_list.di.VacancyModule;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;
import com.dmelnyk.workinukraine.utils.buttontab.ButtonTabs;
import com.dmelnyk.workinukraine.utils.buttontab.ImageButtonBehavior;

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
    @BindView(R.id.settings_image_button) ImageButton mSettingsImageButton;

    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;

    private static Map<String, Integer> sTabVacancyCount;
    private String[] mTabTitles;
    private boolean orientationHasChanged;

    @OnClick(R.id.favorite_image_view)
    public void onViewClicked() {
        onExit();
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

        // sets result to update
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

        if (savedInstanceState == null) {
            sTabVacancyCount = null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mSettingsImageButton = (ImageButton) findViewById(R.id.settings_image_button);
        mSettingsImageButton.setOnClickListener(view -> startFilterActivity());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mSettingsImageButton.getLayoutParams();
        params.setBehavior(new ImageButtonBehavior());
//        mSettingsImageButton.setLayoutParams(params);
        mSettingsImageButton.requestLayout();

        mButtonTabs.setOnTabClickListener(tabClicked -> {
            mViewPager.setCurrentItem(tabClicked);

            updateTitleView(tabClicked);
        });
        mButtonTabs.setSaveEnabled(true);
    }

    private void startFilterActivity() {
        Intent intent = new Intent(this, FilterActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, (View) mSettingsImageButton, getString(R.string.fab_transition));
            ActivityCompat.startActivity(this, intent, option.toBundle());
        } else {
            startActivity(intent);
        }
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
//        outState.putIntArray(KEY_TAB_VACANCY_COUNT, sTabVacancyCount);
        outState.putBoolean(KEY_ORIENTATION_CHANGED, true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // restoring ButtonTab position
        if (savedInstanceState != null) {
            int currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION);
            mTabTitles = savedInstanceState.getStringArray(KEY_TAB_TITLES);
//            sTabVacancyCount = savedInstanceState.getIntArray(KEY_TAB_VACANCY_COUNT);
            orientationHasChanged = savedInstanceState.getBoolean(KEY_ORIENTATION_CHANGED);
            updateTitleView(currentPosition);
        }
    }

    @Override
    public void onFragmentInteractionItemClicked(VacancyModel vacancyModel, List<VacancyModel> vacancies) {
        // Activity with multiple vacancy
        Intent vacancyContainerIntent = VacancyViewerActivity.getIntent(this, vacancies, vacancyModel);
        startActivityForResult(vacancyContainerIntent, WEBVIEW_REQUEST_CODE);
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
            Map<String, Integer> tabVacancyCount,
            int buttonTabType,
            Map<String, List<VacancyModel>> allVacancies) {

        // initialize ButtonTubs
        initializeButtonTabs(buttonTabType);
        // Initialize tab titles
        mTabTitles = tabTitles;
        sTabVacancyCount = tabVacancyCount;
        mSlideAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), mTabTitles, allVacancies);
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

    public void initializeButtonTabs(int buttonTabType) {
        int[][] resource;
        if (buttonTabType == 2) {
            resource = new int[4][2]; // 4 buttons (all, new, recent, favorite)
        } else {
            resource = new int[3][2]; // 3 buttons (all, new / recent, favorite)
        }

        resource[0][0] = R.mipmap.ic_tab_vacancy_light;
        resource[0][1] = R.mipmap.ic_tab_vacancy_dark;

        switch (buttonTabType) {
            case 1: // all, new, favorite
                resource[1][0] = R.mipmap.ic_tab_new_light;
                resource[1][1] = R.mipmap.ic_tab_new_dark;
                resource[2][0] = R.mipmap.ic_tab_favorite_light;
                resource[2][1] = R.mipmap.ic_tab_favorite_dark;
                break;
            case 2: // all, new, recent, favorite
                resource[1][0] = R.mipmap.ic_tab_new_light;
                resource[1][1] = R.mipmap.ic_tab_new_dark;
                resource[2][0] = R.mipmap.ic_tab_recent_light;
                resource[2][1] = R.mipmap.ic_tab_recent_dark;
                resource[3][0] = R.mipmap.ic_tab_favorite_light;
                resource[3][1] = R.mipmap.ic_tab_favorite_dark;
                break;
            case 3: // all, recent, favorite
                resource[1][0] = R.mipmap.ic_tab_recent_light;
                resource[1][1] = R.mipmap.ic_tab_recent_dark;
                resource[2][0] = R.mipmap.ic_tab_favorite_light;
                resource[2][1] = R.mipmap.ic_tab_favorite_dark;
                break;
        }

        mButtonTabs.setData(resource);
        mButtonTabs.setVisibility(View.VISIBLE);
    }

    private void updateTitleView(int tabPosition) {
        mTitleTextView.setText(mTabTitles[tabPosition]);
        // TODO: edit this!!!
//        mTitleVacanciesCountTextView.setText("" + sTabVacancyCount[tabPosition]);
        mButtonTabs.selectTab(tabPosition);
        Log.e("333", "mButtonTabs.selectTab=" + tabPosition);
    }

    @Override
    public void updateFavoriteTab(List<VacancyModel> vacancies) {
        // updating data in adapters
        mSlideAdapter.updateFavoriteData(vacancies);
        // updating title manually if current tab is FAVORITE;
        // TODO: edit this!!!
//        sTabVacancyCount[2] = vacancies.size();
        if (mViewPager.getCurrentItem() == 2) {
            updateTitleView(2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("999", "onActivityResult in VacancyActivity");
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
