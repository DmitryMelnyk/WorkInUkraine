package com.dmelnyk.workinukraine.ui.dialogs.request;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.BaseDialog;
import com.dmelnyk.workinukraine.utils.MyBounceInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dmitry on 15.03.17.
 */


public class DialogRequest extends BaseDialog implements
        Contract.View
{
    public static String REQUEST;
    private DialogRequestCallbackListener mCallbackInterface;

    @BindView(R.id.closeButton) ImageView closeButton;
    @BindView(R.id.textInputLayout) TextInputLayout textInputLayout;
    @BindView(R.id.search_dialog_keywords) AppCompatEditText searchRequest;
    @BindView(R.id.search_dialog_spinner) Spinner spinner;
    @BindView(R.id.button_ok) Button button;

    private View dialogView;
    private static DialogRequest dialog;
    private DialogRequestPresenter presenter;

    public static DialogRequest getInstance() {
        if (null == dialog) {
            dialog = new DialogRequest();
        }
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_request2, container, false);
        ButterKnife.bind(this, dialogView);

        initializePresenter();
        configSpinner();
        presenter.bindView(this);

        return dialogView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.bindView(null);
        presenter = null;
    }

    @Override
    public void dialogDismiss() {
        dismiss();
    }

    private void initializePresenter() {
        if (presenter == null) {
            presenter = new DialogRequestPresenter();
        }
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getActivity(), getResources().getString
                (R.string.minimal_request_length), Toast.LENGTH_SHORT).show();
        startSearchRequestAnimation();
    }

    private void startSearchRequestAnimation() {
        // initialize animations;
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
        scaleAnimation.setInterpolator(interpolator);
        textInputLayout.startAnimation(scaleAnimation);
    }

    @Override
    public void configSpinner() {
        String[] cities = (getResources().getStringArray(R.array.cities));
        Arrays.sort(cities);
        List<String> items = new ArrayList<>();
        items.add("Киев");
        Collections.addAll(items, cities);

        spinner.setSelection(0);
        spinner.getBackground().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.violet_lighter),
                PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(),
                R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnClick({R.id.closeButton, R.id.button_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closeButton:
                dismiss();
                break;
            case R.id.button_ok:
                String textRequest = String.valueOf(searchRequest.getText());
                String cityRequest = String.valueOf(spinner.getSelectedItem());
                if (REQUEST == null) {
                    REQUEST = new String();
                }
                REQUEST = textRequest + " / " + cityRequest;

                if (mCallbackInterface != null) {
                    mCallbackInterface.onTakeRequest(REQUEST);
                }

                presenter.onButtonClicked(textRequest, cityRequest);
                break;
        }
    }

    public interface DialogRequestCallbackListener {
        void onTakeRequest(String request);
    }

    public void setCallback(DialogRequestCallbackListener callbackInterface) {
        mCallbackInterface = callbackInterface;
    }
}
