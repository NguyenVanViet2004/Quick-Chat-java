package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private userModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setInformation();

    }

    private void initView(){
        MyApplication.applyNightMode();
        binding.backFragmentMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.nightSwitch.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Nút chuyển đổi chế độ tối
        binding.nightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.toggleNightMode();
            }
        });

        //nút chỉnh sửa thông tin cá nhân
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,EditProfileActivity.class));
            }
        });

        //nút đăng xuất
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut(); // Gọi phương thức logout khi nút được nhấn
            }
        });
        binding.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink();
            }
        });
    }



    private void setInformation() {
        firebaseUtil.currentUserDetails().get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(userModel.class);
                    if (userModel != null){
                        binding.fullName.setText(userModel.getUsername());
                        // xu ly avt
                        firebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId())
                                .getDownloadUrl().addOnCompleteListener(task1 ->{
                                    if (task1.isSuccessful()) firebaseUtil.setAvatar(SettingActivity.this,task1.getResult(), binding.itemAvatar);
                        });
                    }
                }
            }
        });
    }

    private void openLink() {
        String facebookUri = "https://www.facebook.com/VietNguyenVan2004";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUri));

        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
        else startActivity(intent);
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setIcon(R.drawable.baseline_warning_24);

        // Nút "Có"
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete fcm token
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        firebaseUtil.logout();
                        // Thêm hành động chuyển hướng đến màn hình đăng nhập sau khi đăng xuất .
                        Intent intent = new Intent(MyApplication.getInstance(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa các activity trên đỉnh ngăn xếp
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Tạo một task mới cho LoginActivity
                        MyApplication.getInstance().startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                });

            }
        });

        // Nút "Không"
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}