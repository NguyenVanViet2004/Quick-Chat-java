package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backFragmentMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //nút chuyển sang chế độ tối
        binding.nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;


                if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }

                recreate();
            }
        });

        //nút chỉnh sửa thông tin cá nhân
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,EditProfileActivity.class));
            }
        });
    }
}