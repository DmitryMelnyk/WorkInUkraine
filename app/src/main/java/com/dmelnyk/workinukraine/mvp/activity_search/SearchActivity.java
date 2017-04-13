package com.dmelnyk.workinukraine.mvp.activity_search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.JobPool;
import com.dmelnyk.workinukraine.di.MyApplication;
import com.dmelnyk.workinukraine.di.component.DaggerDbComponent;
import com.dmelnyk.workinukraine.di.component.DbComponent;
import com.dmelnyk.workinukraine.mvp.activity_tabs.TabsActivity;
import com.dmelnyk.workinukraine.mvp.dialog_downloading.DialogDownloading;
import com.dmelnyk.workinukraine.mvp.dialog_request.DialogRequest;

import javax.inject.Inject;

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
//                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                    Fragment prev = getSupportFragmentManager().findFragmentByTag(DIALOG_REQUEST);
//                    if (prev != null){
//                        ft.remove(prev);
//                    }
//
//                    DialogRequest dialog =
//                            DialogRequest.getInstance(mainActivityHandler);
//                    dialog.show(ft, DIALOG_REQUEST);
//        });
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
