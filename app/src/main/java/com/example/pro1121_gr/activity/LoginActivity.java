package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginBinding;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    CountryCodePicker countryCodePicker;
    EditText edt_login;
    Button btnLoginNext;
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