package com.dmelnyk.workinukraine.ui.navigation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.ui.navigation.Contract.INavigationPresenter;
import com.dmelnyk.workinukraine.ui.navigation.di.NavigationModule;
import com.dmelnyk.workinukraine.ui.navigation.menu.DrawerAdapter;
import com.dmelnyk.workinukraine.ui.navigation.menu.DrawerItem;
import com.dmelnyk.workinukraine.ui.navigation.menu.SimpleItem;
import com.dmelnyk.workinukraine.ui.navigation.menu.SpaceItem;
import com.dmelnyk.workinukraine.ui.search.SearchFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import javax.inject.Inject;

import butterknife.BindView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NavigationActivity extends AppCompatActivity implements
        DrawerAdapter.OnItemSelectedListener, Contract.INavigationView,
        SearchFragment.OnFragmentInteractionListener
{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container)
    FrameLayout container;

    private TextView nearestAlarm;

    @Inject
    INavigationPresenter presenter;
    @Inject
    NavUtil navUtil;

    private static final int NAV_SEARCH_POSITION = 0;
    private static final int NAV_REFRESH_POSITION = 1;
    private static final int NAV_FAVORITES_POSITION = 2;
    private static final int NAV_BLACKLIST_POSITION = 3;
    private static final int NAV_SETTINGS_POSITION = 4;
    private static final int NAV_EXIT_POSITION = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;
    private SlidingRootNav navigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // initialize dagger2
        WorkInUaApplication.get(this).getAppComponent()
                .add(new NavigationModule()).inject(this);

        navigator = new SlidingRootNavBuilder(this)
//                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_drawer)
                .inject();

        nearestAlarm = (TextView) findViewById(R.id.menu_nearest_alarm);

        screenTitles = navUtil.getNavTitles();
        screenIcons = navUtil.getNavIcons();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(NAV_SEARCH_POSITION).setChecked(true),
                createItemFor(NAV_REFRESH_POSITION),
                createItemFor(NAV_FAVORITES_POSITION),
                createItemFor(NAV_BLACKLIST_POSITION),
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
    }

    @Override
    public void onItemSelected(int position) {
        Fragment fragment = null;

        switch (position) {
            case NAV_EXIT_POSITION:
                finish();
                return;
            case NAV_SEARCH_POSITION:
                fragment = new SearchFragment();
                break;
            case NAV_REFRESH_POSITION:
                // TODO
                break;
            case NAV_FAVORITES_POSITION:
                // TODO
                break;
            case NAV_BLACKLIST_POSITION:
                // TODO
                break;
            case NAV_SETTINGS_POSITION:
                // TODO
                break;
        }
        // TODO: remove string below after implementing TODOs above
//        fragment = new Fragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        navigator.closeMenu();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.violet))
                .withSelectedTextTint(color(R.color.violet));
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
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
    public void restoreSavedState(String time) {
        // TODO
    }

    @Override
    public void onFragmentInteraction() {
        navigator.openMenu();
    }
}
