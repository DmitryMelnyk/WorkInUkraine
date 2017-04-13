package com.dmelnyk.workinukraine.mvp.activity_settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dmelnyk.workinukraine.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        configToolbar();
        configSettingsFragment();
    }

    private void configSettingsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new FragmentSettings())
                .commit();
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings_activity_title);
        toolbar.setNavigationOnClickListener(
                view -> finish());
    }
}
