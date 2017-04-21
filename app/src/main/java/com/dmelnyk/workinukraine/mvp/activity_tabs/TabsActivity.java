package com.dmelnyk.workinukraine.mvp.activity_tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.ImageUtils;
import com.dmelnyk.workinukraine.helpers.Job;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.dmelnyk.workinukraine.mvp.activity_tabs.TabRecycler.KEY_JOB_LIST;

public class TabsActivity extends AppCompatActivity
        implements Contract.View, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "GT.TabsActivity";

    private TabsActivityPresenter presenter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewPager) ViewPager pager;
    @BindView(R.id.tabLayout) SmartTabLayout tab;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.spinner_checking) AVLoadingIndicatorView spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.bind(this);

        configToolbar();
        configFAB();
        configNavigationDrawer();

        initializePresenter();
        presenter.onTakeView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations())
            presenter.onTakeView(null);
            presenter = null;
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new TabsActivityPresenter(this);
        }
    }

    public final static String HEADHUNTERSUA = "HEADHUNTERSUA";
    public final static String JOBSUA        = "JOBSUA";
    public final static String RABOTAUA      = "RABOTAUA";
    public final static String WORKNEWINFO   = "WORKNEWINFO";
    public final static String WORKUA        = "WORKUA";

    public void onConfigSmartTabs(Bundle allJobs) {
        Log.d(TAG, "configSmartTabs()");

        // change visibility of title image
        ImageView collapsingImage = (ImageView) findViewById(R.id.collapsing_image);
        collapsingImage.setVisibility(View.VISIBLE);

        final HashMap<String, String> keysMap = new HashMap<>();
        keysMap.put(HEADHUNTERSUA, "hh.ua");
        keysMap.put(JOBSUA, "jobs.ua");
        keysMap.put(RABOTAUA, "rabota.ua");
        keysMap.put(WORKNEWINFO, "worknew.info");
        keysMap.put(WORKUA, "work.ua");

        HashMap<String, Bundle> dataSet = new HashMap<>();
        // put proper data to recyclerView
        for (String key : keysMap.keySet()) {
            Bundle arg = new Bundle();
            ArrayList<Job> jobs = allJobs.getParcelableArrayList(key);
            if (jobs!= null && jobs.size() > 0) {
                arg.putParcelableArrayList(KEY_JOB_LIST, jobs);
                dataSet.put(keysMap.get(key), arg);
            }
        }

        FragmentPagerItems pages = new FragmentPagerItems(this);
        for (String key : dataSet.keySet()) {
            FragmentPagerItem item = FragmentPagerItem.of(key, TabRecycler.class, dataSet.get(key));
            pages.add(item);
//            Log.d(TAG, dataSet.get(key).toString());
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        pager.setAdapter(adapter);
        tab.setViewPager(pager);
        adapter.notifyDataSetChanged();

        // ImageUtils for getting image to header
        ImageUtils utils = new ImageUtils();

        tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                String title = (String) adapter.getPageTitle(position);
                Integer imageId = utils.getImageId(title);
                collapsingImage.setImageResource(imageId);
            }

            @Override
            public void onPageSelected(int position) { /* NOP*/ }

            @Override
            public void onPageScrollStateChanged(int state) { /* NOP*/ }
        });
    }

    @Override
    public void onShowNetworkErrorMessage() {
        Toast.makeText(this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void configNavigationDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configFAB() {
        fab.setOnClickListener(fab -> presenter.onButtonClicked());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        presenter.onNavigationItemSelected(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // For calligraphy fonts
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
        finish();
    }
}
