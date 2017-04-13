package com.dmelnyk.workinukraine.mvp.activity_settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.dmelnyk.workinukraine.R;

/**
 * Created by dmitry on 03.03.17.
 */

public class FragmentSettings extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener, Contract.View {

    private FragmentSettingsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        initializePresenter();
        presenter.onTakeView(this);
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new FragmentSettingsPresenter(getActivity());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onTakeView(null);
        presenter = null;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) { /*NOP*/ }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        presenter.onChangeSettings(sharedPreferences, key);
    }
}
