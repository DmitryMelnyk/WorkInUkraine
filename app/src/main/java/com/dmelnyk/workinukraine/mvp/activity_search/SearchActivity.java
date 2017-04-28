package com.dmelnyk.workinukraine.mvp.activity_search;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.dmelnyk.workinukraine.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity
    implements Contract.View {

    private SearchActivityPresenter presenter;

    @BindView(R.id.searchButton) Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        configSearchButton();

        initializePresenter();
        presenter.onTakeView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onTakeView(null);
        presenter = null;
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new SearchActivityPresenter(this);
        }
    }

    private void configSearchButton() {
        searchButton.setOnClickListener(view -> presenter.onSearchButtonClicked()); //{
    }

    // For calligraphy fonts
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }
}
