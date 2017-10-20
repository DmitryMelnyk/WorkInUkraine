package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.di.DaggerVacancyViewerComponent;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyViewerActivity extends BaseAnimationActivity
    implements Contract.IVacancyViewerView, VacancyFragment.CallbackListener {

    public static final String DATA_FAVORITE = "favorite";
    public static final String DATA_NEW = "new";
    public static final String DATA_RECENT = "recent";
    public static final String DATA_SITE = "site";

    private static final String EXTRA_TYPE = "extra_type";
    private static final String EXTRA_VACANCY_TO_DISPLAY = "extra_display_vacancy";

    @BindView(R.id.vacancy_container) ViewPager mVacancyContainer;

    @Inject
    Contract.IVacancyViewerPresenter presenter;
    List<VacancyModel> mVacancies;
    private VacancyAdapter mAdapter;

    private String mRequest;
    private String mSite;
    private String mType;
    private VacancyModel mVacancyToDisplay;

    public static Intent getIntent(
            Context context,
            VacancyModel vacancyToOpen,
            String type) {
        Intent intent = new Intent(context, VacancyViewerActivity.class);
        intent.putExtra(EXTRA_VACANCY_TO_DISPLAY, vacancyToOpen);
        intent.putExtra(EXTRA_TYPE, type);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacancy_container);
        ButterKnife.bind(this);

        // Inject dependency
        DaggerVacancyViewerComponent.builder()
                .dbModule(new DbModule(getApplicationContext()))
                .build().inject(this);

        // gets vacancy to show position
        mVacancyToDisplay = getIntent().getParcelableExtra(EXTRA_VACANCY_TO_DISPLAY);
        mRequest = mVacancyToDisplay.request();
        mSite = mVacancyToDisplay.site();
        mType = getIntent().getStringExtra(EXTRA_TYPE);

        presenter.bindView(this);
        presenter.getData(mRequest, mType, mSite);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void displayVacancies(List<VacancyModel> vacancies) {
        mVacancies = vacancies;
        mAdapter = new VacancyAdapter(getSupportFragmentManager(), mVacancies);
        mVacancyContainer.setAdapter(mAdapter);
        mVacancyContainer.setCurrentItem(findPosition(mVacancyToDisplay, mVacancies));
    }

    private int findPosition(VacancyModel vacancyToDisplay, List<VacancyModel> vacancies) {
        for (int i = 0; i < vacancies.size(); i++) {
            if (vacancies.get(i).equals(vacancyToDisplay)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onExit() {
        onBackPressed();
    }

    @Override
    public void updateFavoriteVacancy(VacancyModel vacancy) {
        presenter.updateFavoriteStatusVacancy(vacancy);
        VacancyModel updatedVacancy = vacancy.getUpdatedFavoriteVacancy();
        Timber.i("updatedVacancy=" + updatedVacancy);
        mVacancies.set(mVacancies.indexOf(vacancy), updatedVacancy);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

    @Override
    public void showUpdatingVacancySuccess(Boolean isFavorite) {
        String msg = isFavorite
                ? getString(R.string.msg_item_saved_to_favorite)
                : getString(R.string.msg_item_removed_from_favorite);

        Snackbar.make(mVacancyContainer, msg, 2000).show();
    }

    @Override
    public void showUpdatingVacancyError() {
        // TODO: move to strings
        Toast.makeText(this, R.string.msg_vacancy_update_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
