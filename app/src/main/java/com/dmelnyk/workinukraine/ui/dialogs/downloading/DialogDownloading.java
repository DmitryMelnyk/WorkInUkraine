package com.dmelnyk.workinukraine.ui.dialogs.downloading;

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
 * Created by dmitry on 15.03.17.
 */


public class DialogDownloading extends BaseDialog
        implements Contract.View {
    private static final String TAG = "GT.DialogDownloading";


    @BindView(R.id.button_ok)
    Button buttonOk;
    @BindView(R.id.rotateLoading)
    RotateLoading rotateLoading;
    @BindView(R.id.downloadingStartedLayout)
    LinearLayout downloadingStartedLayout;
    @BindView(R.id.downloadingFinishedLayout)
    LinearLayout downloadingFinishedLayout;

    private View dialogView;
    private static Handler mainActivityHandler;
    private static DialogDownloading dialog;
    private DialogDownloadingPresenter presenter;

    public static DialogDownloading getInstance(Handler handler) {
        mainActivityHandler = handler;
        if (null == dialog) {
            dialog = new DialogDownloading();
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_downloading2, container, false);
        ButterKnife.bind(this, dialogView);

//        initializePresenter();
//        presenter.onTakeView(this);
        rotateLoading.start();
        return dialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onTakeView(null);
        presenter = null;
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new DialogDownloadingPresenter(getActivity(), mainActivityHandler);
        }
    }

    @Override
    public void dialogDismiss() {
        animateDismissDialog();
    }

    // TODO: replace downloading implementation
    @OnClick(R.id.button_ok)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                rotateLoading.stop();
                rotateLoading.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadingStartedLayout.setVisibility(View.GONE);
                        downloadingFinishedLayout.setVisibility(View.VISIBLE);
                        buttonOk.setEnabled(true);
                    }
                }, 600);
        }
    }
}
