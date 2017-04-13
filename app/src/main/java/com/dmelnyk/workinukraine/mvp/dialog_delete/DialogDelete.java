package com.dmelnyk.workinukraine.mvp.dialog_delete;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.BaseDialog;
import com.dmelnyk.workinukraine.mvp.activity_favorite_recent_new.BaseActivity.ActivityType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dmitry on 01.04.17.
 */

public class DialogDelete extends BaseDialog implements
        View.OnClickListener, Contract.View {
    private static final String TAG = "GT.DialogDelete";

    @BindView(R.id.buttonCancel)
    Button buttonCancel;
    @BindView(R.id.buttonRemove)
    Button buttonRemove;

    private View dialogView;
    private static DialogDelete dialog;
    private DialogDeletePresenter presenter;
    private static ActivityType typeActivity;

    public static DialogDelete getInstance(ActivityType type) {
        typeActivity = type;
        if (null == dialog) {
            dialog = new DialogDelete();
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_delete, container, false);
        ButterKnife.bind(this, dialogView);

        configButtons();

        initializePresenter();
        presenter.onTakeView(this);
        return dialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onTakeView(null);
        presenter = null;
    }

    private void configButtons() {
        buttonRemove.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new DialogDeletePresenter(getActivity(), typeActivity);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCancel:
                presenter.onButtonClicked(ButtonType.CANCEL);
                break;
            case R.id.buttonRemove:
                presenter.onButtonClicked(ButtonType.OK);
        }
    }

    @Override
    public void dialogDismiss() {
        animateDismissDialog();
    }

    public void closeActivity() {
        Toast.makeText(getActivity(), R.string.dialog_delete_clear_toast, Toast.LENGTH_SHORT).show();
        getActivity().setContentView(R.layout.activity_base_empty);

        getActivity().finish();
    }
}
