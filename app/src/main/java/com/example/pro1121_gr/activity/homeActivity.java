package com.example.pro1121_gr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityHomeBinding;
import com.example.pro1121_gr.fragments.ChatFragment;
import com.example.pro1121_gr.function.ReplaceFragment;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import es.dmoral.toasty.Toasty;

public class homeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    private boolean doubleBackToExitPressedOnce = false;
    String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();
        idUser = FirebaseUtil.currentUserId();
        getFMCtoken();
        initView();


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
        FirebaseUtil.currentUserDetails().update("status",1);
    }

    private void getFMCtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) Log.e(homeActivity.class.getSimpleName(), "getFMCtoken: " + task.getResult() );
            FirebaseUtil.currentUserDetails().update("fmctoken",task.getResult());
        });
    }


    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        DBhelper.getInstance(this).endUsageTracking();
        FirebaseFirestore.getInstance().collection("users").document(idUser).update("status",0);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            FirebaseFirestore.getInstance().collection("users").document(idUser).update("status",0);
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
}