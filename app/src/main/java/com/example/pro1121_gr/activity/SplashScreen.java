package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySplashScreenBinding;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.example.pro1121_gr.util.firebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    private NetworkChangeReceiver networkChangeReceiver;

    private final String TAG = SplashScreen.class.getSimpleName();


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

        if (firebaseUtil.isLoggedIn() && getIntent().getExtras() != null){
            String userID = getIntent().getExtras().getString("userId");
            Log.e(TAG, "userID: " + userID );
            if (userID != null) {
                firebaseUtil.allUserCollectionReference().document(userID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        userModel model = task.getResult().toObject(userModel.class);
                        Log.e(TAG, "model : " + model.getFMCToken() );

                        Intent mainIntent = new Intent(this, home.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(mainIntent);

                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        StaticFunction.passUserModelAsIntent(intent, model);
                        startActivity(intent);
                        finish();
                    }
                });
            }else gotoHome();
        }else gotoHome();
    }

    private Boolean isLogin(){
        if (firebaseUtil.isLoggedIn()) return true;
        return false;
    }

    private void gotoHome(){
        new Handler().postDelayed(() ->{
            if (isLogin()){
                startActivity(new Intent(this, home.class)); finish();
            }else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }, 2000);
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