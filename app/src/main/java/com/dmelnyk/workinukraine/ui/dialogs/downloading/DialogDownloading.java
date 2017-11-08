package com.dmelnyk.workinukraine.ui.dialogs.downloading;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final String ARG_IS_DOWNLOADING = "animation_is_on";
    private static final java.lang.String ARG_TOTAL_VACANCIES_COUNT = "total_vacancies_count";
    @BindView(R.id.button_cancel) Button buttonCancel;
    @BindView(R.id.button_ok) Button buttonOk;
    @BindView(R.id.rotateLoading) RotateLoading rotateLoading;
    @BindView(R.id.downloadingStartedLayout) LinearLayout downloadingStartedLayout;
    @BindView(R.id.downloadingFinishedLayout) LinearLayout downloadingFinishedLayout;
    @BindView(R.id.vacancy_count_text_view) TextView mVacancyCountTextView;
    @BindView(R.id.ll_vacancies_count) LinearLayout mVacanciesCountLinearLayout;
    @BindView(R.id.tv_vacancies_count) TextView mVacanciesCountTextView;
    Unbinder unbinder;
    private CallbackLister mCallback;

    public static DialogDownloading newInstance(boolean isDownloading, int totalVacanciesCount) {
        DialogDownloading dialog = new DialogDownloading();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_DOWNLOADING, isDownloading);
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

        boolean isDownloading = getArguments().getBoolean(ARG_IS_DOWNLOADING);
        if (isDownloading) {
            rotateLoading.start();
        } else {
            int totalVacanciesCount = getArguments().getInt(ARG_TOTAL_VACANCIES_COUNT);
            showFinishedView(totalVacanciesCount);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void downloadingFinished(int count) {
        Timber.d("downloadingFinished()");
        rotateLoading.stop();
        mVacanciesCountLinearLayout.setVisibility(View.GONE);
        showFinishedView(count);
    }

    public void updateVacanciesCount(int count) {
//        mVacanciesCountLinearLayout.setVisibility(View.VISIBLE);
        mVacanciesCountTextView.setText("" + count);

    }

    private void showFinishedView(int count) {
        downloadingStartedLayout.setVisibility(View.GONE);
        downloadingFinishedLayout.setVisibility(View.VISIBLE);
        mVacancyCountTextView.setText("" + count);
        buttonCancel.setVisibility(View.GONE);
        buttonOk.setEnabled(true);
        buttonOk.setVisibility(View.VISIBLE);
    }

    @OnClick({ R.id.button_ok, R.id.button_cancel })
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_cancel:
                mCallback.onCancelClickedDownloadingDialog();
                dismiss();
                break;
            case R.id.button_ok:
                mCallback.onOkClickedInDownloadingDialog();
                dismiss();
                break;
        }
    }

    public void setCallback(CallbackLister callback) {
        this.mCallback = callback;
    }

    public interface CallbackLister {
        void onOkClickedInDownloadingDialog();

        void onCancelClickedDownloadingDialog();
    }
}
