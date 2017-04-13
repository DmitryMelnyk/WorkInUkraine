package com.dmelnyk.workinukraine.mvp.dialog_request;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.BaseDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dmitry on 15.03.17.
 */


public class DialogRequest extends BaseDialog
        implements View.OnClickListener, Contract.View {
    private static final String TAG = "GT.DialogRequest";

    @BindView(R.id.search_dialog_keywords) EditText searchRequest;
    @BindView(R.id.search_dialog_spinner) Spinner spinner;
    @BindView(R.id.button_ok) Button button;

    private View dialogView;
    private static Handler mainActivityHandler;
    private static DialogRequest dialog;
    private DialogRequestPresenter presenter;

    public static DialogRequest getInstance(Handler handler) {
        mainActivityHandler = handler;
        if (null == dialog) {
            dialog = new DialogRequest();
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_request, container, false);
        ButterKnife.bind(this, dialogView);

        configButton();
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

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new DialogRequestPresenter(getActivity(), mainActivityHandler);
        }
    }

    private void configButton() {
        button.setOnClickListener(this);
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getActivity(), getResources().getString
                (R.string.minimal_request_length), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void configSpinner(ArrayList<String> items) {
        spinner.setSelection(0);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(),
                R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        String textRequest = String.valueOf(searchRequest.getText());
        String cityRequest = String.valueOf(spinner.getSelectedItem());
        presenter.onButtonClicked(textRequest, cityRequest);
    }

    @Override
    public void dialogDismiss() {
        animateDismissDialog();
    }
}
