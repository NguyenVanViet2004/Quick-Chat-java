package com.example.pro1121_gr.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityHomeBinding;
import com.example.pro1121_gr.fragments.ChatFragment;
import com.example.pro1121_gr.function.MyApplication;
import com.example.pro1121_gr.function.ReplaceFragment;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.util.ExitAppEvent;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class homeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getValueSharedPreferences();
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();
        Functions.getIdUser();
        getFMCtoken();
        initView();
    }

    private void getValueSharedPreferences(){
        SharedPreferences preferences = getSharedPreferences("language", MODE_PRIVATE);
        boolean vn = preferences.getBoolean("isVietnamese", false);
        boolean en = preferences.getBoolean("isEnglish", false);
        boolean ge = preferences.getBoolean("isGermany", false);
        if (vn) setLocale("vi");
        else if (en) setLocale("en");
        else if (ge) setLocale("de");
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        getBaseContext().getResources().updateConfiguration(
                config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    private void initView() {
        setSatus();
        ReplaceFragment.replaceFragment(
                this.getSupportFragmentManager(),
                R.id.frame_layout,
                new ChatFragment(),
                false
        );

        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottomNavMessage){
                ReplaceFragment.replaceFragment(
                        homeActivity.this.getSupportFragmentManager(),
                        R.id.frame_layout,
                        new ChatFragment(),
                        false
                );
            } else {
                startActivity(new Intent(homeActivity.this, SettingActivity.class));
            }
            return true;
        });
    }

    private void setSatus(){
        UserDAO.currentUserDetails().update("status",1);
    }

    private void getFMCtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.e(homeActivity.class.getSimpleName(), "getFMCtoken: " + task.getResult() );
            UserDAO.currentUserDetails().update("fmctoken",task.getResult());
        });
    }


    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        DBhelper.getInstance(this).endUsageTracking();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            FirebaseFirestore.getInstance().collection("users").document(Functions.getIdUser()).update("status",0);
            return;
        }
        Toasty.warning(this,"Nhấn lần nữa để thoát", Toasty.LENGTH_LONG, true).show();
        this.doubleBackToExitPressedOnce = true;


        // Đặt thời gian chờ để reset trạng thái doubleBackToExitPressedOnce
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                },
                2000 // 2 giây
        );
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any necessary data here, including language settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("lang", getCurrentLanguage()).apply();
    }

    private String getCurrentLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("lang", "vi");
    }*/
}