package com.dmelnyk.workinukraine.ui.dialogs.delete;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 7/23/17.
 */

public class DialogDelete extends BaseDialog {

    private static final String ARG_TITLE = "title";
    DialogDeleteCallbackListener mCallback;
    Unbinder unbinder;
    @BindView(R.id.request_text_view) TextView mTextViewTitle;

    public static DialogDelete getInstance(String title) {
        DialogDelete dialog = new DialogDelete();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_delete2, container, false);
        unbinder = ButterKnife.bind(this, dialogView);
        String title = getArguments().getString(ARG_TITLE);
        mTextViewTitle.setText(title);
        return dialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.buttonCancel, R.id.buttonRemove})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonCancel:
                dismiss();
                break;
            case R.id.buttonRemove:
                if (mCallback != null) {
                    mCallback.onRemoveRequest();
                } else throw new ClassCastException("DialogDelete.DialogDeleteCallbackListener not implemented!");
                dismiss();
                break;
        }
    }

    public interface DialogDeleteCallbackListener {
        void onRemoveRequest();
    }

    public void setCallback(DialogDeleteCallbackListener callback) {
        mCallback = callback;
    }
}
