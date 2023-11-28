package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;

import android.widget.Toast;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    //    CountryCodePicker countryCodePicker;
    EditText edt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        edt_login = findViewById(R.id.edt_login);

        binding.countryCodePicker.registerCarrierNumberEditText(binding.edtLogin);

        binding.btnLoginNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    Intent intent = new Intent(LoginActivity.this, LoginActivityWithOTP.class);
                    intent.putExtra("phone", binding.countryCodePicker.getFullNumberWithPlus());
                    intent.putExtra("phoneAndNoCodeCountry", binding.edtLogin.getText().toString().trim());
                    startActivity(intent);
                } else
                    Toast.makeText(LoginActivity.this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private Boolean isValid(){
        return binding.countryCodePicker.isValidFullNumber() && binding.countryCodePicker.getFullNumberWithPlus().length() >= 10;
    }
}