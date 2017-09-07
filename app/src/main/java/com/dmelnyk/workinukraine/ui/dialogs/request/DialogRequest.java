package com.dmelnyk.workinukraine.ui.dialogs.request;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.IntDef;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.ui.search.SearchAdapter;
import com.dmelnyk.workinukraine.utils.BaseDialog;
import com.dmelnyk.workinukraine.utils.MyBounceInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by d264 on 9/2/17.
 */

public class DialogRequest extends BaseDialog {

    public static final String ARG_EDIT_REQUEST = "edit_request";
    public static final int MODE_EDIT_REQUEST = 1;
    public static final int MODE_ADD_REQUEST = 2;

    @IntDef ({MODE_ADD_REQUEST, MODE_EDIT_REQUEST})
    public @interface MODE {}

    @BindView(R.id.search_dialog_spinner) Spinner mCitySpinner;
    @BindView(R.id.search_dialog_keywords) AppCompatEditText mRequestTextInputLayout;
    @BindView(R.id.ok_button) Button mOkButton;
    @BindView(R.id.textInputLayout) TextInputLayout mTextInputLayout;
    Unbinder unbinder;
    private DialogRequestCallbackListener mCallback;

    private Bundle mArgs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // args == null in case of adding new request
        // and != null in case of editing old request
        mArgs = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_request, container);
        unbinder = ButterKnife.bind(this, view);

        String request = null;
        String city = null;
        if (mArgs != null) {
            String fullRequest = mArgs.getString(ARG_EDIT_REQUEST);
            request = fullRequest.split(" / ")[0];
            city = fullRequest.split(" / ")[1];
        }

        configSpinner(request, city);
        return view;
    }

    public void configSpinner(@Nullable String request, @Nullable String city) {
        String[] cities = (getResources().getStringArray(R.array.cities));
        Arrays.sort(cities);
        List<String> items = new ArrayList<>();
        items.add(getResources().getString(R.string.city_kiev));
        Collections.addAll(items, cities);

        // in case of editing request
        if (city != null && request != null) {
            // restores request
            mRequestTextInputLayout.setSelected(true);
            mRequestTextInputLayout.setText(request);

            // restores city
            int position = Collections.binarySearch(items, city);
            mCitySpinner.setSelection(position);
        } else {
            // sets "Kiev" to first position in spinner
            mCitySpinner.setSelection(0);
        }

        mCitySpinner.getBackground().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(),
                R.layout.spinner_item, items);
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        mCitySpinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        sendFeedback();
    }

    private void sendFeedback() {
        mCallback.dialogDismissed();
        mCallback = null;
    }

    private void startErrorAnimation() {
        // initialize animations;
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
        scaleAnimation.setInterpolator(interpolator);
        mTextInputLayout.startAnimation(scaleAnimation);
    }

    @OnClick(R.id.ok_button)
    public void onViewClicked() {
        if (isRequestCorrect()) {
            int mode = mArgs != null
                    ? MODE_EDIT_REQUEST
                    : MODE_ADD_REQUEST;

            mCallback.onTakeRequest(getRequest(), mode);
            mCallback = null;
            dismiss();
        } else {
            // show error notification with animation
            startErrorAnimation();
            Toast.makeText(getActivity(), getResources().getString
                    (R.string.minimal_request_length), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isRequestCorrect() {
        return mRequestTextInputLayout.getText().toString().length() >= 3;
    }

    private String getRequest() {
        String city = (String) mCitySpinner.getSelectedItem();
        String request = mRequestTextInputLayout.getText().toString();
        return request + " / " + city;
    }

    public interface DialogRequestCallbackListener {
        void onTakeRequest(String request, @MODE int mode);

        void dialogDismissed();
    }

    public void setCallback(DialogRequestCallbackListener callbackInterface) {
        mCallback = callbackInterface;
    }


}
