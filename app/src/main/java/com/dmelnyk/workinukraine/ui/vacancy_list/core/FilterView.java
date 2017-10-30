package com.dmelnyk.workinukraine.ui.vacancy_list.core;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.dmelnyk.workinukraine.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by d264 on 10/12/17.
 */

public class FilterView extends ConstraintLayout implements
        FilterAdapter.CallbackListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private boolean isEnable;
//    private Context mContext;

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Switch mSwitcher;

    private FilterAdapter mAdapter;

    private CallbackListener mCallback;
    private List<String> wordsList;
    private Set<String> originalData;
    private boolean originalCheckedState;

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setData(Pair<Boolean, Set<String>> data, CallbackListener callbackListener) {
        originalCheckedState = data.first;
        originalData = data.second;

        isEnable = data.first;
        wordsList = new ArrayList<>(data.second);
        mCallback = callbackListener;
        initData();
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout = (ConstraintLayout)
                inflater.from(context).inflate(R.layout.view_filter, this, false);

        mRecyclerView = constraintLayout.findViewById(R.id.recyclerView);
        mSwitcher = constraintLayout.findViewById(R.id.switcher);
        mSwitcher.setOnCheckedChangeListener(this);
        mEditText = constraintLayout.findViewById(R.id.et_item);
        mEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
                        && mEditText.getText().length() >= 3) {
                    addWord(mEditText.getText().toString());
                }
                return false;
            }
        });
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (view.getId() == R.id.et_item) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        constraintLayout.findViewById(R.id.button_add).setOnClickListener(this);
        constraintLayout.findViewById(R.id.button_cancel).setOnClickListener(this);
        constraintLayout.findViewById(R.id.button_ok).setOnClickListener(this);

        addView(constraintLayout);
    }

    private void initData() {
        mAdapter = new FilterAdapter(wordsList, isEnable, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwitcher.setChecked(isEnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                addWord(mEditText.getText().toString());
                break;
            case R.id.button_cancel:
                restoreState();
                mCallback.filterCancel();
                break;
            case R.id.button_ok:
                callback();
                break;
        }
    }

    private void restoreState() {
        wordsList.clear();
        wordsList.addAll(originalData);
        mAdapter.notifyDataSetChanged();

        mSwitcher.setChecked(originalCheckedState);
        mEditText.getText().clear();
        mEditText.clearFocus();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        mAdapter = new FilterAdapter(wordsList, checked, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    // Call callback
    private void callback() {
        mCallback.updateFilter(new Pair<>(mSwitcher.isChecked(), new HashSet<String>(wordsList)));
    }

    private void addWord(String word) {
        String toast;
        if (!wordsList.contains(word)) {
            wordsList.add(word);
            mAdapter.notifyDataSetChanged();
            mEditText.getText().clear();
            mSwitcher.setChecked(true);
            toast = word + " " + getResources().getString(R.string.msg_item_added);
        } else {
            toast = getContext().getString(R.string.msg_item_already_exists_in_filter);
        }

        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private void clearData() {
        mSwitcher.setChecked(false);
        wordsList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoveClicked(String item) {
        wordsList.remove(item);
        mAdapter.notifyDataSetChanged();
    }

    public interface CallbackListener {
        void updateFilter(Pair<Boolean, Set<String>> data);
        void filterCancel();
    }
}
