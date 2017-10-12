package com.dmelnyk.workinukraine.ui.vacancy_list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.BaseTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.FilterAdapter;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.FilterView;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.ScreenSlidePagerAdapter;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.SitesTabFragment;
import com.dmelnyk.workinukraine.ui.vacancy_list.core.VacancyCardViewAdapter;
import com.dmelnyk.workinukraine.ui.vacancy_list.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.ui.vacancy_list.di.VacancyModule;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.VacancyViewerActivity;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;
import com.dmelnyk.workinukraine.utils.buttontab.ButtonTabs;
import com.dmelnyk.workinukraine.utils.buttontab.ImageButtonBehavior;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyListActivity extends BaseAnimationActivity implements
        BaseTabFragment.OnFragmentInteractionListener,
        SitesTabFragment.OnFragmentInteractionListener,
        FilterAdapter.CallbackListener,
        Contract.IVacancyView, FilterView.CallbackListener {

    private static final String KEY_CURRENT_POSITION = "KEY_CURRENT_POSITION";
    private static final String KEY_TAB_TITLES = "KEY_TAB_TITLES";
    private static final String KEY_TAB_VACANCY_COUNT = "KEY_TAB_VACANCY_COUNT";
    private static final String KEY_ORIENTATION_CHANGED = "KEY_ORIENTATION_CHANGED";
    private static final String KEY_TOOLBAR_DEFAULT_HEIGHT = "KEY_TOOLBAR_DEFAULT_HEIGHT";
    private static final String KEY_SETTINGS_FLAG = "KEY_SETTINGS_FLAG";
    private static final String TAG = "TAG";
    private static final String KEY_REVEAL_X = "KEY_REVEAL_X";
    private static final String KEY_REVEAL_Y = "KEY_REVEAL_Y";
    private static final int WEBVIEW_REQUEST_CODE = 1002;

    @Inject
    Contract.IVacancyPresenter presenter;

    @BindView(R.id.title_text_view) TextView mTitleTextView;
    @BindView(R.id.vacancies_count_text_view) TextView mTitleVacanciesCountTextView;
    @BindView(R.id.pager) ViewPager mViewPager;
    @BindView(R.id.button_tabs) ButtonTabs mButtonTabs;
    @BindView(R.id.settings_image_button) ImageButton mSettingsImageButton;
    @BindView(R.id.animationContainer) FrameLayout animationContainer;
    @BindView(R.id.app_bar) AppBarLayout appBar;

    private ScreenSlidePagerAdapter mSlideAdapter;
    private String mRequest;

    private int[] mTabVacancyCount;
    private String[] mTabTitles;
    private boolean orientationHasChanged;
    private int mButtonTabType; // look initializeButtonTabs() function
    private FilterView animationLayout;

    private Animation alphaAnim;
    private Animation rotateRightAnim;
    private Animation rotateLeftAnim;
    private boolean flag = true;
    private int mHeight;
    private int toolbarHeight;
    private int revealX;
    private int revealY;
    private ValueAnimator expandingHeightAnimator;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        initializeFilterView(savedInstanceState);
        presenter.bindView(this, mRequest);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // For fonts
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mSettingsImageButton = (ImageButton) findViewById(R.id.settings_image_button);
        mSettingsImageButton.setOnClickListener(view -> launchFilterAnimation(mSettingsImageButton));
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mSettingsImageButton.getLayoutParams();
        params.setBehavior(new ImageButtonBehavior());
        mSettingsImageButton.requestLayout();

        mButtonTabs.setOnTabClickListener(tabClicked -> {
            mViewPager.setCurrentItem(tabClicked);

            updateTitleView(tabClicked);
        });
        mButtonTabs.setSaveEnabled(true);

    }

    private void initializeFilterView(Bundle savedInstanceState) {
        animationLayout = (FilterView) findViewById(R.id.filter_view);
        animationLayout.setData(presenter.getFilterData(), this);

        alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        rotateRightAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        rotateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.rotate_left);

        if (savedInstanceState != null) {
            flag = savedInstanceState.getBoolean(KEY_SETTINGS_FLAG);
            toolbarHeight = savedInstanceState.getInt(KEY_TOOLBAR_DEFAULT_HEIGHT);
            revealX = savedInstanceState.getInt(KEY_REVEAL_X);
            revealY = savedInstanceState.getInt(KEY_REVEAL_Y);
            Log.d(TAG, "flag=" + flag);
        }

        // measuring height of filter view. Then hid it.
        animationLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mHeight = animationLayout.getHeight();
                        animationLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        if (mHeight < toolbarHeight) {
                            mHeight = toolbarHeight;
                        }

                        if (flag) {
                            animationLayout.setVisibility(View.INVISIBLE);
                            animationContainer.setVisibility(View.GONE);
                        }
                    }
                }
        );
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.clear();
        presenter.updateVacanciesTimeStatus();
        Log.e("VLA", "onDestroy()");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, mViewPager.getCurrentItem());
        outState.putStringArray(KEY_TAB_TITLES, mTabTitles);
        outState.putIntArray(KEY_TAB_VACANCY_COUNT, mTabVacancyCount);
        outState.putBoolean(KEY_ORIENTATION_CHANGED, true);
        outState.putBoolean(KEY_SETTINGS_FLAG, flag);
        outState.putInt(KEY_TOOLBAR_DEFAULT_HEIGHT, toolbarHeight);
        outState.putInt(KEY_REVEAL_X, revealX);
        outState.putInt(KEY_REVEAL_Y, revealY);
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
    public void onFragmentInteractionItemClicked(VacancyModel vacancyModel) {
        // Activity with multiple vacancy
        String type = getCurrentTabType();
        Intent vacancyContainerIntent = VacancyViewerActivity.getIntent(this, vacancyModel, type);
        startActivityForResult(vacancyContainerIntent, WEBVIEW_REQUEST_CODE);
    }

    /**
     * Types: DATA_SITE, DATA_NEW, DATA_RECENT, DATA_FAVORITE
     *
     * @return The type of active tab in viewPager
     */
    private String getCurrentTabType() {
        String type = null;
        switch (mViewPager.getCurrentItem()) {
            case 0:
                type = VacancyViewerActivity.DATA_SITE;
                break;
            case 1:
                if (mButtonTabType == 1 || mButtonTabType == 2) {
                    type = VacancyViewerActivity.DATA_NEW;
                } else {
                    type = VacancyViewerActivity.DATA_RECENT;
                }
                break;
            case 2:
                if (mButtonTabType == 2) {
                    type = VacancyViewerActivity.DATA_RECENT;
                } else {
                    type = VacancyViewerActivity.DATA_FAVORITE;
                }
                break;
            case 3:
                type = VacancyViewerActivity.DATA_FAVORITE;
                break;
        }

        return type;
    }

    @Override
    public void onFragmentInteractionPopupMenuClicked(VacancyModel vacancy,
                                                      @VacancyCardViewAdapter.VacancyPopupMenuType int type) {
        presenter.onItemPopupMenuClicked(vacancy, type);
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
            int buttonTabType,
            Map<String, List<VacancyModel>> allVacancies) {

        // Initialize tab titles
        mTabTitles = tabTitles;
        mTabVacancyCount = tabVacancyCount;
        Log.e("ATTA", "mTabVacancyCount=" + mTabVacancyCount.toString());

        mButtonTabType = buttonTabType;
        // initialize ButtonTubs
        initializeButtonTabs(buttonTabType);
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
        mTitleVacanciesCountTextView.setText("" + mTabVacancyCount[tabPosition]);
        mButtonTabs.selectTab(tabPosition);
    }

    @Override
    public void updateFavoriteTab(List<VacancyModel> vacancies) {
        // updating data in adapters
        if (mSlideAdapter != null) {
            mSlideAdapter.updateFavoriteData(vacancies);
            // updating title manually if current tab is FAVORITE;
            mTabVacancyCount[2] = vacancies.size();
            if (mViewPager.getCurrentItem() == 2) {
                updateTitleView(2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEBVIEW_REQUEST_CODE) {
            // call to load fresh list of favorites vacancies
            presenter.bindJustView(this);
        }
    }

    @Override
    public void onRemoveClicked(String item) {
        Toast.makeText(this, "onRemoveItemClicked=" + item, Toast.LENGTH_SHORT).show();
    }

    // triggered by clicking imageButton
    public void launchFilterAnimation(View view) {
        if (flag) {
            // start and end positions of image button (reveal starting point) are the same
            revealX = (int) (mSettingsImageButton.getX() + mSettingsImageButton.getWidth() / 2);
            revealY = (int) (mSettingsImageButton.getY() + mSettingsImageButton.getHeight() / 2);

            toolbarHeight = appBar.getHeight();
            int hypotenuse = (int) Math.hypot(appBar.getWidth(), appBar.getHeight());
//            mSettingsImageButton.setBackgroundResource(R.drawable.rounded_cancell_button);
//            mSettingsImageButton.setImageResource(R.mipmap.image_cancel);
            Log.e("!!!", "revealX=" + revealX + ", revealY=" + revealY);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) animationContainer.getLayoutParams();
            params.width = appBar.getWidth();
            params.height = appBar.getHeight();
            animationContainer.setLayoutParams(params);
            animationContainer.setVisibility(View.VISIBLE);

            final Animator revealAnim = ViewAnimationUtils.createCircularReveal(animationContainer, revealX, revealY, 0, hypotenuse);
            revealAnim.setDuration(300);
            revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            revealAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animationLayout.setVisibility(View.VISIBLE);
                    animationLayout.startAnimation(alphaAnim);

                    createExpandingHeightAnimation(toolbarHeight, mHeight);
                    expandingHeightAnimator.start();
                    mSettingsImageButton.startAnimation(rotateRightAnim);
                }
            });

            revealAnim.start();
            flag = false;
        } else {
            // reduce to 200 dp
            revealX = (int) (mSettingsImageButton.getX() + mSettingsImageButton.getWidth() / 2);
            revealY = mHeight;
            mSettingsImageButton.startAnimation(rotateLeftAnim);

            createExpandingHeightAnimation(mHeight, toolbarHeight);
            expandingHeightAnimator.start();
            expandingHeightAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    int hypotenuse = (int) Math.hypot(animationContainer.getWidth(), animationContainer.getHeight());
                    Animator revealAnim = ViewAnimationUtils.createCircularReveal(animationContainer, revealX, revealY, hypotenuse, 0);
                    revealAnim.setDuration(300);
                    revealAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationContainer.setVisibility(View.GONE);
                            animationLayout.setVisibility(View.INVISIBLE);

//                            mSettingsImageButton.setBackgroundResource(R.drawable.rounded_button);
//                            mSettingsImageButton.setImageResource(R.mipmap.ic_settings);
                        }
                    });

                    revealAnim.start();
                    flag = true;
                }
            });
        }
    }

    private void createExpandingHeightAnimation(int startHeight, int finalHeight) {
        expandingHeightAnimator = ValueAnimator.ofInt(startHeight, finalHeight);
        expandingHeightAnimator.setDuration(getResources().getInteger(R.integer.anim_duration_short));
        expandingHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = animationContainer.getLayoutParams();
                layoutParams.height = val;
                animationContainer.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    public void updateFilter(Pair<Boolean, Set<String>> data) {
        Toast.makeText(this, "filterUpdated TODO! " + data.second + " isEnable=" + data.first,
                Toast.LENGTH_SHORT).show();
        presenter.filterUpdated(data);
        launchFilterAnimation(mSettingsImageButton);
    }
}
