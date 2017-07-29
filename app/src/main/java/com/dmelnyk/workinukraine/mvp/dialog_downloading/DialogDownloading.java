package com.dmelnyk.workinukraine.mvp.dialog_downloading;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.BaseDialog;
import com.victor.loading.rotate.RotateLoading;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by dmitry on 12.03.17.
 */

public class DialogDownloading extends BaseDialog
        implements Contract.View {

    public static final String TAG = "GT.DialogDownloading";
    private final static int HEADHUNTERSUA = 0;
    private final static int JOBSUA = 1;
    private final static int RABOTAUA = 2;
    private final static int WORKNEWINFO = 3;
    private final static int WORKUA = 4;

    private static Handler uiHandler;
    private static String requestText;
    private static String requestCity;

    @BindView(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @BindView(R.id.downloadingStartedLayout)
    LinearLayout downloadingStartedLayout;
    @BindView(R.id.downloadingFinishedLayout)
    LinearLayout downloadingFinishedLayout;
    private DialogDownloadPresenter presenter;

    @BindView(R.id.button_ok)
    Button buttonOk;

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
        View view = inflater.inflate(R.layout.dialog_downloading2, container, false);
        ButterKnife.bind(this, view);

        rotateLoading.start();
        setCancelable(false);

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

    @Override
    public void dialogDismiss() {
        dismiss();
    }

    // TODO: remove
    @Override
    public void updateLoader(int loaderCode, int size) {
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
        rotateLoading.stop();
        buttonOk.setEnabled(true);
        downloadingStartedLayout.setVisibility(View.GONE);
        downloadingFinishedLayout.setVisibility(View.VISIBLE);
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.button_ok)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                presenter.onButtonClicked();
                break;
        }
    }
}
