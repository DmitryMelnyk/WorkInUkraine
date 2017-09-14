package com.dmelnyk.workinukraine.ui.vacancy_viewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.db.di.DbModule;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.ui.vacancy_viewer.di.DaggerVacancyViewerComponent;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyViewerActivity extends BaseAnimationActivity
    implements Contract.IVacancyViewerView, VacancyFragment.CallbackListener {

    private static final String EXTRA_VACANCIES = "extra_vacancies";
    private static final String EXTRA_VACANCY_TO_DISPLAY = "extra_display_vacancy";
    @BindView(R.id.vacancy_container) ViewPager mVacancyContainer;

    @Inject
    Contract.IVacancyViewerPresenter presenter;
    ArrayList<VacancyModel> mVacancies;

    public static Intent getIntent(Context context, List<VacancyModel> vacancies, VacancyModel openVacancy) {
        Intent intent = new Intent(context, VacancyViewerActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_VACANCIES,
                (ArrayList<? extends Parcelable>) vacancies);
        intent.putExtra(EXTRA_VACANCY_TO_DISPLAY, openVacancy);
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

        mVacancies = getIntent().getParcelableArrayListExtra(EXTRA_VACANCIES);
        VacancyModel vacancyToDisplay = getIntent().getParcelableExtra(EXTRA_VACANCY_TO_DISPLAY);

        int showVacancyPosition = findPosition(vacancyToDisplay);

        VacancyAdapter adapter = new VacancyAdapter(getSupportFragmentManager(), mVacancies);
        mVacancyContainer.setAdapter(adapter);
        mVacancyContainer.setCurrentItem(showVacancyPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unbindView();
    }

    private int findPosition(VacancyModel vacancyToDisplay) {
        for (int i = 0; i < mVacancies.size(); i++) {
            if (mVacancies.get(i).equals(vacancyToDisplay)) {
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

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdatingVacancyError() {
        // TODO: move to strings
        Toast.makeText(this, "Ошибка обновления избранной вакансии!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
