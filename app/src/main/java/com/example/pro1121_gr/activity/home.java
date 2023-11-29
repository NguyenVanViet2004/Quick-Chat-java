package com.example.pro1121_gr.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityHomeBinding;
import com.example.pro1121_gr.fragments.ChatFragment;
import com.example.pro1121_gr.fragments.ContactsFragment;
import com.example.pro1121_gr.function.ReplaceFragment;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation;

import java.util.Locale;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class home extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean doubleBackToExitPressedOnce = false;
    private String currentLanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        LanguageHelper.loadLocale(this);

        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();
        getFMCtoken();
        initView();

        Intent intent = getIntent();
        if (intent != null) {
            String languageCode = intent.getStringExtra("languageCode");

        }


    }

    private void initView() {
        ReplaceFragment.replaceFragment(
                this.getSupportFragmentManager(),
                R.id.frame_layout,
                new ChatFragment(),
                false
        );

        // Khởi tạo và đăng ký BroadcastReceiver
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        /*binding.bottomNavigation.add(new CurvedBottomNavigation.Model(1, "Tin nhắn", R.drawable.baseline_message_24));
        binding.bottomNavigation.add(new CurvedBottomNavigation.Model(2, "Thêm", R.drawable.ic_baseline_add_24));
        binding.bottomNavigation.add(new CurvedBottomNavigation.Model(3, "Cài đặt", R.drawable.baseline_settings_24));


        binding.bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    ReplaceFragment.replaceFragment(getSupportFragmentManager(), R.id.frame_layout, new ChatFragment(), true);
                    break;
                case 2:
                    showBottomDialog();
                    break;
                case 3:
                    startActivity(new Intent(home.this, SettingActivity.class));
                    break;
            }
            return null;
        });*/

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottomNavMessage){
                ReplaceFragment.replaceFragment(
                        home.this.getSupportFragmentManager(),
                        R.id.frame_layout,
                        new ChatFragment(),
                        false
                );
            }/*
            else if (item.getItemId() == R.id.bottomNavOption){
                showBottomDialog();
            }else  if (item.getItemId() == R.id.bottomNavContact){
                ReplaceFragment.replaceFragment(
                        home.this.getSupportFragmentManager(),
                        R.id.frame_layout,
                        new ContactsFragment(),
                        false
                );
            }
            */
            else {
                startActivity(new Intent(home.this, SettingActivity.class));
            }
            return true;
        });
    }

    private void getFMCtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.e(home.class.getSimpleName(), "getFMCtoken: " + task.getResult() );
            FirebaseUtil.currentUserDetails().update("fmctoken",task.getResult());
        });
    }

    private void showBottomDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        MyApplication.applyNightMode();

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
            window.setGravity(Gravity.BOTTOM);
        }
    }


    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toasty.warning(this,"Nhấn lần nữa để thoát", Toasty.LENGTH_LONG, true).show();

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
    // chỗ xử lý chuyển đổi ngôn ngữ

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any necessary data here, including language settings
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putString("lang", getCurrentLanguage()).apply();
    }

    private String getCurrentLanguage() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("lang", "vi");
    }

}