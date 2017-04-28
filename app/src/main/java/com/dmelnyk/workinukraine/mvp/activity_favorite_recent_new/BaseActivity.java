package com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.CardViewAdapter;
import com.dmelnyk.workinukraine.helpers.Job;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivity;
import com.dmelnyk.workinukraine.mvp.dialog_delete.DialogDelete;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity
    implements Contract.View {

    private int typeActivity;

    public static final int FAVORITE = -1;
    public static final int RECENT = -2;
    public static final int NEW = -3;

    @IntDef({ FAVORITE, RECENT, NEW })
    @Retention(RetentionPolicy.CLASS)
    public @interface ActivityType {}

    public static final String ACTIVITY_TYPE = "TypeActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private BaseActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeActivity = getIntent().getIntExtra(ACTIVITY_TYPE, -4);

        initializePresenter();
        presenter.onTakeView(this, typeActivity);
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new BaseActivityPresenter(this);
        }
    }

    public void onSetEmptyBaseView() {
        setContentView(R.layout.activity_base_empty);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    public void onSetBaseView(ArrayList<Job> jobs) {
        setContentView(R.layout.activity_base);
        ButterKnife.bind(this);
        configFAB();
    }

    public void onConfigRecyclerView(ArrayList<Job> jobs, @CardViewAdapter.CardViewType int type) {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CardViewAdapter adapter = new CardViewAdapter(jobs, this, type);
        recyclerView.setAdapter(adapter);
    }

    public void configToolbar(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        toolbar.setNavigationOnClickListener(
                view -> {
                    if (typeActivity == NEW) {
                        startTabsActivity();
                    } else {
                        onBackPressed();
                    }
                });
    }

    private void configFAB() {
        fab.setOnClickListener(view -> presenter.onButtonClicked());
    }

    // For calligraphy fonts
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onShowDialogDelete() {
        DialogDelete.getInstance(typeActivity).show(getSupportFragmentManager(), "DialogDelete");
    }

    private void startTabsActivity() {
        Intent intent = new Intent(this, TabsActivity.class);
        startActivity(intent);
    }
}
