package com.dmelnyk.workinukraine.ui.vacancy_webview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.models.VacancyModel;
import com.dmelnyk.workinukraine.utils.BaseAnimationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VacancyContainerActivity extends BaseAnimationActivity
    implements VacancyFragment.CallbackListener {

    private static final String EXTRA_VACANCIES = "extra_vacancies";
    private static final String EXTRA_VACANCY_TO_DISPLAY = "extra_display_vacancy";
    @BindView(R.id.vacancy_container)

    ViewPager mVacancyContainer;
    ArrayList<VacancyModel> mVacancies;

    public static Intent getIntent(Context context, List<VacancyModel> vacancies, VacancyModel openVacancy) {
        Intent intent = new Intent(context, VacancyContainerActivity.class);
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

        mVacancies = getIntent().getParcelableArrayListExtra(EXTRA_VACANCIES);
        VacancyModel vacancyToDisplay = getIntent().getParcelableExtra(EXTRA_VACANCY_TO_DISPLAY);

        int showVacancyPosition = findPosition(vacancyToDisplay);

        VacancyAdapter adapter = new VacancyAdapter(getSupportFragmentManager(), mVacancies);
        mVacancyContainer.setAdapter(adapter);
        mVacancyContainer.setCurrentItem(showVacancyPosition);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onExit() {
        onBackPressed();
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }
}
