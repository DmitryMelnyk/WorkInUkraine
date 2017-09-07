package com.dmelnyk.workinukraine.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.application.WorkInUaApplication;
import com.dmelnyk.workinukraine.ui.dialogs.period_chooser.DialogPeriodChooser;
import com.dmelnyk.workinukraine.ui.dialogs.time_picker.DialogTimePicker;
import com.dmelnyk.workinukraine.ui.settings.Contract.ISettingsPresenter;
import com.dmelnyk.workinukraine.ui.settings.di.SettingsModule;
import com.dmelnyk.workinukraine.utils.BaseFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class SettingsFragment extends BaseFragment implements
        Contract.ISettingsView,
        DialogPeriodChooser.OnDialogPeriodInteractionListener,
        DialogTimePicker.OnDialogTimePickerInteractionListener {

    private static final String DIALOG_PERIOD_TAG = "dialog period tag";
    private static final String WEBSTORE_APP_ADDRESS = "http://www.example.com";

    @BindView(R.id.updatePeriod) TextView mUpdatePeriod;
    @BindView(R.id.soundSwitcherText) TextView mSoundSwitcherText;
    @BindView(R.id.soundSwitcher) Switch mSoundSwitcher;
    @BindView(R.id.vibroSwitcherText) TextView mVibroSwitcherText;
    @BindView(R.id.vibroSwitcher) Switch mVibroSwitcher;
    @BindView(R.id.disturbSwitcher) Switch mDisturbSwitcher;
    @BindView(R.id.sendFeedback) LinearLayout mSendFeedback;
    @BindView(R.id.about) LinearLayout mAbout;
    @BindView(R.id.vibroItem) LinearLayout mVibroItem;
    @BindView(R.id.text_view_from) TextView mTextViewFrom;
    @BindView(R.id.text_view_to) TextView mTextViewTo;
    Unbinder unbinder;

    private OnFragmentInteractionListener mListener;

    @Inject
    ISettingsPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WorkInUaApplication.get(getContext()).getAppComponent()
                .add(new SettingsModule()).inject(this);

        Log.e("1010", "SettingsFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);

        presenter.bindView(this);
        initializeSwitcherListeners();
        return view;
    }

    private void initializeSwitcherListeners() {
        mSoundSwitcher.setOnCheckedChangeListener((button, isChecked) -> {
            presenter.onSwitcherChecked(0, isChecked);
        });
        mVibroSwitcher.setOnCheckedChangeListener((button, isChecked) -> {
            presenter.onSwitcherChecked(1, isChecked);
        });
        mDisturbSwitcher.setOnCheckedChangeListener((button, isChecked) -> {
            presenter.onSwitcherChecked(2, isChecked);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDialogPeriodInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        closeMainMenuCallback();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void initializeViews(boolean[] checkedStates, String[] textStates) {
        // Initialize switchers
        mSoundSwitcher.setChecked(checkedStates[0]);
        mVibroSwitcher.setChecked(checkedStates[1]);
        mDisturbSwitcher.setChecked(checkedStates[2]);

        // Initialize text items
        mUpdatePeriod.setText(textStates[0]);
        mSoundSwitcherText.setText(textStates[1]);
        mVibroSwitcherText.setText(textStates[2]);
        mTextViewFrom.setText(getString(R.string.time_picker_from) + " " + textStates[3]);
        mTextViewTo.setText(getString(R.string.time_picker_to) + " " + textStates[4]);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.unbindView();
    }

    @OnClick({R.id.backImageView, R.id.periodItem, R.id.soundItem, R.id.vibroItem, R.id.disturbItem,
            R.id.text_view_from, R.id.text_view_to, R.id.sendFeedback, R.id.about, R.id.thumbUp, R.id.share, R.id.male})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backImageView:
                // opens navigation drawer
                openMainMenuCallback();
                break;
            case R.id.periodItem:
                presenter.onPeriodItemClicked();
                break;
            case R.id.soundItem:
                presenter.onSwitcherChecked(0, !mSoundSwitcher.isChecked());
                break;
            case R.id.vibroItem:
                presenter.onSwitcherChecked(1, !mVibroSwitcher.isChecked());
                break;
            case R.id.disturbItem:
                presenter.onSwitcherChecked(2, !mDisturbSwitcher.isChecked());
                break;
            case R.id.text_view_from:
                presenter.onDontDisturbTimeModeChose(SettingsPresenter.TIME_FROM);
                break;
            case R.id.text_view_to:
                presenter.onDontDisturbTimeModeChose(SettingsPresenter.TIME_TO);
                break;
            case R.id.sendFeedback:
                break;
            case R.id.about:
                break;
            case R.id.thumbUp:
                Intent openInWebStore = new Intent(Intent.ACTION_VIEW, Uri.parse(WEBSTORE_APP_ADDRESS));
                startActivity(openInWebStore);
                break;
            case R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
//                TODO: change address of app
                share.putExtra(Intent.EXTRA_TEXT, WEBSTORE_APP_ADDRESS);
                startActivity(share);
                break;
            case R.id.male:
                Intent mail = new Intent(Intent.ACTION_SENDTO);
                mail.setType("text/plain");
//                mail.putExtra(Intent.EXTRA_EMAIL, "dmitrydev264@gmail.com");
                mail.putExtra(Intent.EXTRA_SUBJECT, "From WorkInUkraine app");
                mail.setData(Uri.parse("mailto:dmitrydev264@gmail.com"));
                startActivity(mail);
                break;
        }
    }

    @Override
    public void showTimePicker(String time) {
        DialogTimePicker timePicker = DialogTimePicker.getNewInstance(time);
        timePicker.setInteractionListener(this);
        timePicker.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void showDialogPeriodChooser(int repeatingPosition) {
        Fragment previous = getFragmentManager().findFragmentByTag(DIALOG_PERIOD_TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (previous != null) {
            ft.remove(previous);
        }
        ft.addToBackStack(null);

        DialogPeriodChooser dialog = DialogPeriodChooser.getNewInstance(repeatingPosition);
        dialog.setInteractionListener(this);
        dialog.show(ft, DIALOG_PERIOD_TAG);
    }

    @Override
    public void onRadioItemChecked(int checkedItemPosition) {
        presenter.onPeriodChose(checkedItemPosition);
        Toast.makeText(getContext(), "Clicked " + checkedItemPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(String time) {
        presenter.onTimeSelected(time);
    }

    @Override
    public void updateFromTextView(String time) {
        mTextViewFrom.setText(getString(R.string.time_picker_from) + " " + time);
    }

    @Override
    public void updateToTextView(String time) {
        mTextViewTo.setText(getString(R.string.time_picker_to) + " " + time);
    }
}
