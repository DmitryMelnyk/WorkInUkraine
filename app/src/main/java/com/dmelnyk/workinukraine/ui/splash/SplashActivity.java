package com.dmelnyk.workinukraine.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dmelnyk.workinukraine.ui.navigation.NavigationActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
        finish();
    }
}
