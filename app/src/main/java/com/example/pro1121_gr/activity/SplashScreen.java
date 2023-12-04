package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.databinding.ActivitySplashScreenBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.function.MyApplication;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.ExitAppEvent;
import com.example.pro1121_gr.util.ServiceUtils;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = SplashScreen.class.getSimpleName();


    @SuppressLint("LogNotTimber")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MyApplication.applyNightMode();
        if (ServiceUtils.isServiceRunning(this, ExitAppEvent.class)) {
            // Dịch vụ đang chạy
            Log.e(TAG, "Dịch vụ đang chạy " );
        } else {
            // Dịch vụ không đang chạy, bạn có thể khởi tạo nó
            Log.e(TAG, "Dịch vụ không đang chạy " );
            Intent serviceIntent = new Intent(this, ExitAppEvent.class);
            startService(serviceIntent);
        }


        if (UserDAO.isLoggedIn() && getIntent().getExtras() != null){
            String userID = getIntent().getExtras().getString("userId");
            Log.e(TAG, "userID: " + userID );
            if (userID != null) {
                UserDAO.allUserCollectionReference().document(userID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        userModel model = task.getResult().toObject(userModel.class);
                        Intent mainIntent = new Intent(this, homeActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainIntent);

                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (model != null) {
                            Functions.passUserModelAsIntent(intent, model);
                        }
                        startActivity(intent);
                        finish();
                    }
                });
            }else gotoHome();
        }else gotoHome();
    }


    private void gotoHome(){
        new Handler().postDelayed(() ->{
            // bắt đầu tính thời gian sử dụng app
            DBhelper.getInstance(this).startUsageTracking();
            if (UserDAO.isLoggedIn()){
                startActivity(new Intent(this, homeActivity.class));
                finish();
            }else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBhelper.getInstance(this).endUsageTracking();
    }
}