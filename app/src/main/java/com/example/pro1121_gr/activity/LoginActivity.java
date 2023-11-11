package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginBinding;
import com.example.pro1121_gr.util.NetworkChangeReceiver;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;
    private ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo và đăng ký BroadcastReceiver
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

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

    @Override
    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }
}