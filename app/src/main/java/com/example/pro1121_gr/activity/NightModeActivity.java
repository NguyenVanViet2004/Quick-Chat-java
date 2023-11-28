package com.example.pro1121_gr.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.databinding.ActivityNightModeBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.example.pro1121_gr.util.NetworkChangeReceiver;

public class NightModeActivity extends AppCompatActivity {
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNightModeBinding nightModeBinding = ActivityNightModeBinding.inflate(getLayoutInflater());
        setContentView(nightModeBinding.getRoot());

        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
        nightModeBinding.nightSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        nightModeBinding.nightSwitch.setOnClickListener(view -> MyApplication.toggleNightMode());
        nightModeBinding.backFragmentMess.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isFinishing()) {
            // Activity is not finishing, perform cleanup operations
            if (networkChangeReceiver != null) {
                unregisterReceiver(networkChangeReceiver);
            }
            DBhelper.getInstance(this).endUsageTracking();
        }
    }

}