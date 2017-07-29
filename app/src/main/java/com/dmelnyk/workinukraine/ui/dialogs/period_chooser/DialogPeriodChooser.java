package com.dmelnyk.workinukraine.ui.dialogs.period_chooser;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.helpers.BaseDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDialogPeriodInteractionListener} interface
 * to handle interaction events.
 */
public class DialogPeriodChooser extends BaseDialog {

    private static final String CHECKED_ITEM = "checked radio button number";

    @BindView(R.id.closeButton)
    ImageView closeButton;
    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;
    @BindView(R.id.button_ok)
    Button buttonOk;
    Unbinder unbinder;
    private OnDialogPeriodInteractionListener mListener;

    /**
     * @param n - The number of selected radio item
     * @return the DialogPeriodChooser
     */
    public static DialogPeriodChooser getNewInstance(int n) {
        DialogPeriodChooser dialog = new DialogPeriodChooser();
        Bundle args = new Bundle();
        args.putInt(CHECKED_ITEM, n);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_period_chooser, container, false);
        unbinder = ButterKnife.bind(this, view);

        int selectedRadioItem = getArguments().getInt(CHECKED_ITEM);
        initializeRadioButtons(selectedRadioItem);

        return view;
    }

    private void initializeRadioButtons(int selectedRadioItem) {
        String[] radioItemsText = getContext().getResources().getStringArray(R.array.settings_period_summary);
        for (int i = 0; i < radioItemsText.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setId(i);
            radioButton.setText(radioItemsText[i]);
            radioButton.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorSecondary));
            radioGroup.addView(radioButton);

            if (selectedRadioItem == i)
                radioButton.setChecked(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.closeButton, R.id.button_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closeButton:
                dismiss();
                break;
            case R.id.button_ok:
                int checkedId = radioGroup.getCheckedRadioButtonId();
                mListener.onRadioItemChecked(checkedId);
                dismiss();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDialogPeriodInteractionListener {
        // TODO: Update argument type and name
        void onRadioItemChecked(int checkedItemPosition);
    }

    public void setInteractionListener(OnDialogPeriodInteractionListener listener) {
        mListener = listener;
    }
}
