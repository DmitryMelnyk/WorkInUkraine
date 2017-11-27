package com.dmelnyk.workinukraine.utils;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.dmelnyk.workinukraine.R;
import com.dmelnyk.workinukraine.utils.buttontab.ButtonTabs;

public class ButtonActivity extends AppCompatActivity {

    ButtonTabs buttonTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        buttonTabs = findViewById(R.id.button_tabs);
    }

    public void fourButtonsClicked(View view) {
        initializeButtonTabs(1);
    }

    public void threeButtonsClicked(View view) {
        initializeButtonTabs(2);
    }

    public void initializeButtonTabs(int buttonTabType) {
        int[][] resource = ButtonTabUtil.getResources(buttonTabType);
        buttonTabs.setData(resource);
        buttonTabs.setVisibility(View.VISIBLE);
    }

}
