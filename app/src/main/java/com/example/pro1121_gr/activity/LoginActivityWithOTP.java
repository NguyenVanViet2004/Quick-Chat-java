package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginWithOtpBinding;

public class LoginActivityWithOTP extends AppCompatActivity {

    private ActivityLoginWithOtpBinding binding;
    private String phoneNumber;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginWithOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = getIntent().getStringExtra("phone");
        binding.titleLoginOTP.setText("VUI LÒNG NHẬP MÃ OTP ĐÃ ĐƯỢC GỬI ĐẾN : " + phoneNumber);

        binding.btnLoginNextOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivityWithOTP.this, CreateProfile.class);
                intent.putExtra("phone", phoneNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });
    }
}