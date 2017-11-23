package com.dmelnyk.workinukraine.ui.dialogs.loading;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dmelnyk.workinukraine.R;

/**
 * Created by d264 on 11/22/17.
 */

public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public static LoadingDialog getInstance(Context context) {
        return new LoadingDialog(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
}
