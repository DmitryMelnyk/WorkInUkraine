package com.dmelnyk.workinukraine.ui.dialogs.downloading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.BaseDialog;
import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by dmitry on 15.03.17.
 */


public class DialogDownloading extends BaseDialog {

    private static final String ARG_ANIMATION_ON = "animation_is_on";
    private static final java.lang.String ARG_TOTAL_VACANCIES_COUNT = "total_vacancies_count";
    @BindView(R.id.button_ok) Button buttonOk;
    @BindView(R.id.rotateLoading) RotateLoading rotateLoading;
    @BindView(R.id.downloadingStartedLayout) LinearLayout downloadingStartedLayout;
    @BindView(R.id.downloadingFinishedLayout) LinearLayout downloadingFinishedLayout;
    @BindView(R.id.vacancy_count_text_view) TextView mVacancyCountTextView;
    Unbinder unbinder;

    public static DialogDownloading newInstance(boolean animationOn, int totalVacanciesCount) {
        DialogDownloading dialog = new DialogDownloading();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ANIMATION_ON, animationOn);
        args.putInt(ARG_TOTAL_VACANCIES_COUNT, totalVacanciesCount);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_downloading, container, false);
        unbinder = ButterKnife.bind(this, view);
        setCancelable(false);

        boolean animationIsOn = getArguments().getBoolean(ARG_ANIMATION_ON);
        if (animationIsOn) {
            rotateLoading.start();
        } else {
            int totalVacanciesCount = getArguments().getInt(ARG_TOTAL_VACANCIES_COUNT);
            downloadingStartedLayout.setVisibility(View.GONE);
            downloadingFinishedLayout.setVisibility(View.VISIBLE);
            mVacancyCountTextView.setText("" + totalVacanciesCount);
            buttonOk.setEnabled(true);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // TODO: add (String, int) and create new view to show how many vacancies has found.
    public void downloadingFinished(int count) {
        Timber.d("downloadingFinished()");
        rotateLoading.stop();
        downloadingStartedLayout.setVisibility(View.GONE);
        downloadingFinishedLayout.setVisibility(View.VISIBLE);
        mVacancyCountTextView.setText("" + count);
        buttonOk.setEnabled(true);
    }

    @OnClick(R.id.button_ok)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                dismiss();
                break;
        }
    }
}
