package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySplashScreenBinding;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.firebaseUtil;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    private NetworkChangeReceiver networkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplication.applyNightMode();

        // Khởi tạo và đăng ký BroadcastReceiver
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        new Handler().postDelayed(() ->{
            if (isLogin()){
                startActivity(new Intent(this, home.class)); finish();
            }else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }, 3000);
    }

    private Boolean isLogin(){
        if (firebaseUtil.isLoggedIn()) return true;
        return false;
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