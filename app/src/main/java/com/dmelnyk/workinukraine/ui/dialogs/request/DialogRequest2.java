package com.dmelnyk.workinukraine.ui.dialogs.request;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
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

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.BaseDialog;
import com.dmelnyk.workinukraine.utils.MyBounceInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by d264 on 9/2/17.
 */

public class DialogRequest2 extends BaseDialog {

    @BindView(R.id.search_dialog_spinner)
    Spinner mCitySpinner;
    @BindView(R.id.search_dialog_keywords)
    AppCompatEditText mRequestTextInputLayout;
    @BindView(R.id.ok_button)
    Button mOkButton;
    Unbinder unbinder;
    @BindView(R.id.textInputLayout)
    TextInputLayout textInputLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_request3, container);
        unbinder = ButterKnife.bind(this, view);

        configSpinner();
        return view;
    }

    public void configSpinner() {
        String[] cities = (getResources().getStringArray(R.array.cities));
        Arrays.sort(cities);
        List<String> items = new ArrayList<>();
        items.add("Киев");
        Collections.addAll(items, cities);

        mCitySpinner.setSelection(0);
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

    private void startSearchRequestAnimation() {
        // initialize animations;
        Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_anim);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.5, 20);
        scaleAnimation.setInterpolator(interpolator);
        textInputLayout.startAnimation(scaleAnimation);
    }
}
