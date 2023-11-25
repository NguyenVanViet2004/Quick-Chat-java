package com.example.pro1121_gr.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.pro1121_gr.databinding.ActivitySplashScreenBinding;
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;

public class SplashScreen extends AppCompatActivity {

    private final String TAG = SplashScreen.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.pro1121_gr.databinding.ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyApplication.applyNightMode();


        if (firebaseUtil.isLoggedIn() && getIntent().getExtras() != null){
            String userID = getIntent().getExtras().getString("userId");
            Log.e(TAG, "userID: " + userID );
            if (userID != null) {
                firebaseUtil.allUserCollectionReference().document(userID).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        userModel model = task.getResult().toObject(userModel.class);
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


    private void gotoHome(){
        new Handler().postDelayed(() ->{
            if (firebaseUtil.isLoggedIn()){
                startActivity(new Intent(this, home.class));
                finish();
            }else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }, 2000);
    }

}