package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySplashScreenBinding;
import com.example.pro1121_gr.util.firebaseUtil;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplication.applyNightMode();

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

}