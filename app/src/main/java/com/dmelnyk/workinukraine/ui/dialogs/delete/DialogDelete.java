package com.dmelnyk.workinukraine.ui.dialogs.delete;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.BaseDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 7/23/17.
 */

public class DialogDelete extends BaseDialog {

    private static final String ARG_TITLE = "title";
    private static final String ARG_REMOVE_CODE = "code";

    public static final String REMOVE_ALL_REQUESTS = "remove_all_requests";
    public static final String REMOVE_ONE_REQUEST = "remove_one_request";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ REMOVE_ALL_REQUESTS, REMOVE_ONE_REQUEST })
    public @interface RemoveCode {}

    DialogDeleteCallbackListener mCallback;
    Unbinder unbinder;
    @BindView(R.id.request_text_view) TextView mTextViewTitle;

    public static DialogDelete getInstance(String title, @RemoveCode String removeCode) {
        DialogDelete dialog = new DialogDelete();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_REMOVE_CODE, removeCode);
        dialog.setArguments(args);
        return dialog;
    }

    String removeCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete, container, false);
        unbinder = ButterKnife.bind(this, view);

        String title = getArguments().getString(ARG_TITLE);
        removeCode = getArguments().getString(ARG_REMOVE_CODE);

        mTextViewTitle.setText(title);
        return view;
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
                    mCallback.onRemoveClicked(removeCode);
                } else throw new ClassCastException("DialogDelete.DialogDeleteCallbackListener not implemented!");
                dismiss();
                break;
        }
    }

    public interface DialogDeleteCallbackListener {
        void onRemoveClicked(@RemoveCode String removeCode);
    }

    public void setCallback(DialogDeleteCallbackListener callback) {
        mCallback = callback;
    }
}
