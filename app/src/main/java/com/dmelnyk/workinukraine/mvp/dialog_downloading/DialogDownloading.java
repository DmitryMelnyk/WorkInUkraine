package com.dmelnyk.workinukraine.mvp.dialog_downloading;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.BaseDialog;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by dmitry on 12.03.17.
 */

public class DialogDownloading extends BaseDialog
    implements Contract.View {

    public static final String TAG = "GT.DialogDownloading";
    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA        = 1;
    private final static int RABOTAUA      = 2;
    private final static int WORKNEWINFO   = 3;
    private final static int WORKUA        = 4;

    private static Handler uiHandler;
    private static String requestText;
    private static String requestCity;
    private DialogDownloadPresenter presenter;

    @BindView(R.id.progress_1) ProgressBar progressBar_1;
    @BindView(R.id.progress_1_text_view) TextView text_1;

    @BindView(R.id.progress_2) ProgressBar progressBar_2;
    @BindView(R.id.progress_2_text_view) TextView text_2;

    @BindView(R.id.progress_3) ProgressBar progressBar_3;
    @BindView(R.id.progress_3_text_view) TextView text_3;

    @BindView(R.id.progress_4) ProgressBar progressBar_4;
    @BindView(R.id.progress_4_text_view) TextView text_4;

    @BindView(R.id.progress_5) ProgressBar progressBar_5;
    @BindView(R.id.progress_5_text_view) TextView text_5;

    @BindView(R.id.button_ok) Button button_ok;
    @BindView(R.id.spinner_checking) AVLoadingIndicatorView spinner;

    public static DialogDownloading newInstance(
            Handler handler, String request, String city) {
        uiHandler = handler;
        requestText = request;
        requestCity = city;
        return new DialogDownloading();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_downloading, container, false);
        ButterKnife.bind(this, view);

        spinner.show();
        setCancelable(false);
        configButton();

        initializePresenter();
        presenter.onTakeView(this);

        return view;
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new DialogDownloadPresenter(getActivity(),
                    uiHandler, requestText, requestCity);
        }
    }

    private void configButton() {
        button_ok.setOnClickListener(view -> presenter.onButtonClicked());
    }


    @Override
    public void dialogDismiss() {
        animateDismissDialog();
    }

    @Override
    public void updateLoader(int loaderCode, int size) {
        switch (loaderCode) {
            case HEADHUNTERSUA:
                progressBar_1.setProgress(100);
                text_1.setText("" + size);
                break;
            case WORKUA:
                progressBar_2.setProgress(100);
                text_2.setText("" + size);
                break;
            case RABOTAUA:
                progressBar_3.setProgress(100);
                text_3.setText("" + size);
                break;
            case JOBSUA:
                progressBar_4.setProgress(100);
                text_4.setText("" + size);
                break;
            case WORKNEWINFO:
                progressBar_5.setProgress(100);
                text_5.setText("" + size);
                break;
        }
    }

    @Override
    public void hideSpinner() {
        new Handler().post(hideSpinner);
    }

    @Override
    public void resetPresenter() {
        presenter = null;
    }

    // hide spinner and show OK-button after finishing downloading
    private Runnable hideSpinner = () -> {
        spinner.hide();
        button_ok.setVisibility(View.VISIBLE);
    };
}
