package com.dmelnyk.workinukraine.ui.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.ui.dialogs.loading.LoadingDialog;
import com.dmelnyk.workinukraine.ui.splash.SplashActivity;
import com.dmelnyk.workinukraine.ui.navigation.Contract.INavigationPresenter;
import com.dmelnyk.workinukraine.ui.navigation.di.NavigationModule;
import com.dmelnyk.workinukraine.ui.navigation.menu.DrawerAdapter;
import com.dmelnyk.workinukraine.ui.navigation.menu.DrawerItem;
import com.dmelnyk.workinukraine.ui.navigation.menu.SimpleItem;
import com.dmelnyk.workinukraine.ui.navigation.menu.SpaceItem;
import com.dmelnyk.workinukraine.ui.search.SearchFragment;
import com.dmelnyk.workinukraine.ui.settings.SettingsFragment;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;
import com.dmelnyk.workinukraine.utils.BaseFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.dmelnyk.workinukraine.ui.search.SearchFragment.ARGS_RUN_SEARCHING;

public class NavigationActivity extends BaseAnimationActivity implements
        Contract.INavigationView,
        DrawerAdapter.OnItemSelectedListener,
        SearchFragment.OnFragmentInteractionListener,
        BaseFragment.OnFragmentInteractionListener {

    public static final String EXTRA_SEARCH_FRAGMENT = "EXTRA_SEARCH_FRAGMENT";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    FrameLayout container;

    private TextView mVacanciesCountTextView;
    private static final String FRAGMENT_SEARCH = SearchFragment.class.getName();

    @Inject INavigationPresenter presenter;
    @Inject NavUtil navUtil;

    private static final int NAV_SEARCH_POSITION = 0;
    private static final int NAV_REFRESH_POSITION = 1;
    private static final int NAV_ABOUT_POSITION = 2;
    private static final int NAV_SETTINGS_POSITION = 3;
    private static final int NAV_EXIT_POSITION = 5;

    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav navigator;
    private boolean wasHomePressed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // initialize dagger2
        WorkInUaApplication.get(this).getAppComponent()
                .add(new NavigationModule()).inject(this);

        navigator = new SlidingRootNavBuilder(this)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.navigation_menu_drawer)
                .inject();

        mVacanciesCountTextView = (TextView) findViewById(R.id.menu_nearest_alarm);

        screenTitles = navUtil.getNavTitles();
        screenIcons = navUtil.getNavIcons();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(NAV_SEARCH_POSITION).setChecked(true),
                createItemFor(NAV_REFRESH_POSITION),
                createItemFor(NAV_ABOUT_POSITION),
                createItemFor(NAV_SETTINGS_POSITION),
                new SpaceItem(48),
                createItemFor(NAV_EXIT_POSITION)));

        adapter.setListener(this);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(NAV_SEARCH_POSITION);

        presenter.bindView(this);

        // open SearchFragment if this activity was started from Notification
        if (getIntent().getBooleanExtra(EXTRA_SEARCH_FRAGMENT, false)) {
            navigator.closeMenu();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        presenter.unbindView();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (navigator.isMenuHidden()) {
            navigator.openMenu(true);
        } else {
            finish();
        }
    }

    @Override
    public void onItemSelected(int position) {
        Fragment fragment = null;

        switch (position) {
            case NAV_SEARCH_POSITION:
                fragment = new SearchFragment();
                break;
            case NAV_REFRESH_POSITION:
                fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_SEARCH);
                if (fragment != null) {
                    replaceFragment(fragment);
                    ((SearchFragment)fragment).showDialogDownloading();
                    return;
                } else {
                    fragment = new SearchFragment();
                    Bundle args = new Bundle();
                    args.putBoolean(ARGS_RUN_SEARCHING, true);
                    fragment.setArguments(args);
                }
                break;
            case NAV_ABOUT_POSITION:
                // TODO
                return;
            case NAV_SETTINGS_POSITION:
                fragment = new SettingsFragment();
                break;
            case NAV_EXIT_POSITION:
                finish();
                return;

        }

        // Checks if user click the same menu item.
        // Closes menu in that case.
        boolean isFragmentInBackStack = replaceFragment(fragment);
        if (isFragmentInBackStack) {
            navigator.closeMenu();
            wasHomePressed = false;
        }
    }

    private boolean replaceFragment(Fragment fragment) {
        String fragmentTag = fragment.getClass().getName();

        FragmentManager fm = getSupportFragmentManager();
        boolean fragmentPopped = fm.popBackStackImmediate(fragmentTag, 0);
        boolean backStackContainsFragment = fm.findFragmentByTag(fragmentTag) != null;

        if (!fragmentPopped && !backStackContainsFragment) {
            fm.beginTransaction()
                    .replace(R.id.container, fragment, fragmentTag)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(fragmentTag)
                    .commit();
        }

        return backStackContainsFragment;
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.colorPrimary))
                .withTextTint(color(R.color.colorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        wasHomePressed = true;
    }

    @Override
    public void onOpenMainMenuCallback() {
        if (navigator.isMenuHidden()) {
            navigator.openMenu();
        }
    }

    @Override
    public void onCloseMainMenuCallback() {
        if (!navigator.isMenuHidden()) {
            // don't restore Fragment when user click Home - button
            // and navagation menu was opened
            if (!wasHomePressed) {
                navigator.closeMenu();
            }
        }
    }

    @Override
    public void setVacanciesCount(int vacancies) {
        mVacanciesCountTextView.setText("" + vacancies);
    }
}
