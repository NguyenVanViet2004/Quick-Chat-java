package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.countryCodePicker.registerCarrierNumberEditText(binding.edtLogin);

        binding.btnLoginNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.countryCodePicker.isActivated()){ binding.edtLogin.setError("số điện thoại không hợp lệ!"); return; }
                else {
                    Intent intent = new Intent(LoginActivity.this, LoginActivityWithOTP.class);
                    intent.putExtra("phone", binding.countryCodePicker.getFullNumberWithPlus());
                    startActivity(intent);
                }

            }
        });
    }
}