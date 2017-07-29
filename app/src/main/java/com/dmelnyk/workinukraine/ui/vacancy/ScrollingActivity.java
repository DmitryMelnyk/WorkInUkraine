package com.dmelnyk.workinukraine.ui.vacancy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.ui.vacancy.di.DaggerVacancyComponent;
import com.dmelnyk.workinukraine.utils.ButtonTabs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class ScrollingActivity extends AppCompatActivity implements
        FavoriteFragment.OnFragmentInteractionListener, Contract.IVacancyView {

    public static final String TABLE_FAVORITE = "favorite";
    public static final String TABLE_RECENT = "recent";

    private FavoriteFragment mContainerFragment;
    private ArrayList mRecentItems;

    @Inject
    Contract.IVacancyPresenter presenter;

    private void initializeDependency() {
        DaggerVacancyComponent.builder().dbModule(new DbModule(getApplicationContext()))
                .build().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        initializeDependency();

        int[][] resource = new int[4][2];
        resource[0][0] = R.mipmap.ic_tab_new_light;
        resource[1][0] = R.mipmap.ic_tab_vacancy_light;
        resource[2][0] = R.mipmap.ic_tab_recent_light;
        resource[3][0] = R.mipmap.ic_tab_favorite_light;

        resource[0][1] = R.mipmap.ic_tab_new_dark;
        resource[1][1] = R.mipmap.ic_tab_vacancy_dark;
        resource[2][1] = R.mipmap.ic_tab_recent_dark;
        resource[3][1] = R.mipmap.ic_tab_favorite_dark;

        ButtonTabs tabs = (ButtonTabs) findViewById(R.id.button_tubs);
//        tabs.setBackground(ContextCompat.getDrawable(this, R.drawable.button_tabs_bg));
        tabs.setData(resource);
        tabs.setOnTabClickListener(new ButtonTabs.OnTabClickListener() {
            @Override
            public void tabSelected(int item) {
                Toast.makeText(ScrollingActivity.this, "item clicked: " + item, Toast.LENGTH_SHORT).show();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(fab.getRootView(), "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecentItems = new ArrayList<>();

        // TODO: TABLE_FAVORITE NOW NOT MATTER
        String mRequest = getIntent().getAction();
        Log.e("!!!", "request = " + mRequest);
        presenter.bindView(this, mRequest, TABLE_RECENT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    public void onFragmentInteractionItemClicked(int position) {
        Toast.makeText(this, "Position clicked = " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentInteractionPopupMenuClicked(int position, int type) {
        Toast.makeText(this, "PopupMenu clicked = " + position + ", " + type, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChangeData(List<VacancyModel> vacancies) {
        mContainerFragment.updateData((ArrayList<VacancyModel>) vacancies);
    }

    @Override
    public void openVacancyInWeb(String url) {
        Toast.makeText(this, "openVacancyInWeb", Toast.LENGTH_SHORT).show();
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
    public void showResultingMessage(@CardViewAdapter.VacancyPopupMenuType int type) {
        switch (type) {
            case CardViewAdapter.SAVE:
                Toast.makeText(this, R.string.msg_item_saved_to_favorite, Toast.LENGTH_SHORT).show();
                break;
            case CardViewAdapter.REMOVE:
                Toast.makeText(this, R.string.msg_item_removed_from_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showErrorMessage(String message) {
        Timber.e("error msg = %s", message);
        Log.e("!!!", message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayLoadingProcess() {
        Toast.makeText(this, "Loading starts", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoadingProcess() {
        Toast.makeText(this, "Loading finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayFragment(String type, List<VacancyModel> vacanciesList) {
        Log.e("!!!",  "displayFragment, vacancies count = " + vacanciesList.size());
        mContainerFragment = FavoriteFragment.getNewInstance((ArrayList<VacancyModel>) vacanciesList, this);
        mContainerFragment.setFavoriteFragmentInteractionListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mContainerFragment)
                .addToBackStack("FavoriteFragmentRecent")
                .commit();
    }
}
