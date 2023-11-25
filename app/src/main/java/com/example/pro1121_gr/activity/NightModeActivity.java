package com.example.pro1121_gr.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pro1121_gr.databinding.ActivityNightModeBinding;

public class NightModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNightModeBinding nightModeBinding = ActivityNightModeBinding.inflate(getLayoutInflater());
        setContentView(nightModeBinding.getRoot());

        nightModeBinding.nightSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        nightModeBinding.nightSwitch.setOnClickListener(view -> MyApplication.toggleNightMode());
        nightModeBinding.backFragmentMess.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}