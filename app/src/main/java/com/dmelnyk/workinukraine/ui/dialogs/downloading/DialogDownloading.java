package com.dmelnyk.workinukraine.ui.dialogs.downloading;

import android.content.DialogInterface;
import android.os.Bundle;
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
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by dmitry on 15.03.17.
 */


public class DialogDownloading extends BaseDialog {

    @BindView(R.id.button_ok) Button buttonOk;
    @BindView(R.id.rotateLoading) RotateLoading rotateLoading;
    @BindView(R.id.downloadingStartedLayout) LinearLayout downloadingStartedLayout;
    @BindView(R.id.downloadingFinishedLayout) LinearLayout downloadingFinishedLayout;
    Unbinder unbinder;

    private DialogDownloadCallbackListener mDialogDownloadCallbackListener;

    public static DialogDownloading newInstance() {
        DialogDownloading dialog = new DialogDownloading();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_downloading2, container, false);
        unbinder = ButterKnife.bind(this, view);

        rotateLoading.start();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // TODO: add (String, int) and create new view to show how much vacancies has founded.
    public void downloadingFinished() {
        Timber.d("downloadingFinished()");
        if (rotateLoading == null) return;

        rotateLoading.stop();
        downloadingStartedLayout.setVisibility(View.GONE);
        downloadingFinishedLayout.setVisibility(View.VISIBLE);
        buttonOk.setEnabled(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        mDialogDownloadCallbackListener.onDismissDialogDownloading();
        super.onDismiss(dialog);
    }

    // TODO: replace downloading implementation
    @OnClick(R.id.button_ok)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_ok:
                dismiss();
                break;
        }
    }

    public interface DialogDownloadCallbackListener {
        void onDismissDialogDownloading();
    }

    public void setDialogDownloadingCallbackListener(DialogDownloadCallbackListener listener) {
        mDialogDownloadCallbackListener = listener;
    }
}
