package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;
import com.example.pro1121_gr.util.firebaseUtil;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplication.applyNightMode();
        binding.backFragmentMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.nightSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Nút chuyển đổi chế độ tối
        binding.nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.toggleNightMode();
                recreate(); // Tạo lại Activity để áp dụng chế độ tối

//                int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
//
//
//                if (currentMode == Configuration.UI_MODE_NIGHT_YES) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                }
//
//                recreate();
            }
        });

        //nút chỉnh sửa thông tin cá nhân
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,EditProfileActivity.class));
            }
        });



        //nút đăng xuất
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout(); // Gọi phương thức logout khi nút được nhấn
            }
        });

    }
    // Phương thức logout
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
        // Thêm hành động chuyển hướng đến màn hình đăng nhập sau khi đăng xuất .
        Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các activity trên đỉnh ngăn xếp
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Tạo một task mới cho LoginActivity
        MyApplication.getInstance().startActivity(intent);
    }


}