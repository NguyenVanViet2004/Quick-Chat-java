package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private userModel userModel;
    private NetworkChangeReceiver networkChangeReceiver;

    String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setInformation();

    }

    private void initView(){
        idUser = FirebaseUtil.currentUserId();
        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
        MyApplication.applyNightMode();
        binding.backFragmentMess.setOnClickListener(view -> onBackPressed());

        binding.option.btnNightMode.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, NightModeActivity.class));
        });


        binding.option.btnUsedTime.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this,UsageTimeStatisticsActivity.class));
        });
        //nút chỉnh sửa thông tin cá nhân
        binding.option.editProfile.setOnClickListener(view ->
                startActivity(new Intent(SettingActivity.this,EditProfileActivity.class)));

        //nút đăng xuất
        binding.logout.setOnClickListener(view -> {
            logOut(); // Gọi phương thức logout khi nút được nhấn
        });

        binding.helpLayout.btnMessenger.setOnClickListener(view -> Functions.openLink(SettingActivity.this));

        binding.helpLayout.btnEmail.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + "nviet7532@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Cần giúp đỡ");
            intent.putExtra(Intent.EXTRA_TEXT, "Viết vấn đề của bạn vào đây");

            startActivity(Intent.createChooser(intent, "Choose an Email Client"));
        });

    }



    private void setInformation() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(userModel.class);
                    if (userModel != null){
                        binding.profile.fullName.setText(userModel.getUsername());
                        // xu ly avt
                        FirebaseUtil.getCurrentOtherProfileImageStorageReference(userModel.getUserId())
                                .getDownloadUrl().addOnCompleteListener(task1 ->{
                                    if (task1.isSuccessful()) FirebaseUtil.setAvatar(SettingActivity.this,task1.getResult(), binding.profile.itemAvatar);
                        });
                    }
                }
            }
        });
    }



    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Bạn có chắc chắn muốn đăng xuất không?");
        builder.setIcon(R.drawable.baseline_warning_24);

        // Nút "Có"
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUtil.currentUserDetails().update("status",0);
                // delete fcm token
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FirebaseUtil.logout();
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

    protected void onDestroy() {
        // Hủy đăng ký BroadcastReceiver khi hoạt động bị hủy
        super.onDestroy();
        if (networkChangeReceiver != null) {
            unregisterReceiver(networkChangeReceiver);
        }
        DBhelper.getInstance(this).endUsageTracking();
        Log.e(CreateProfile.class.getSimpleName(), "onDestroy: " + idUser );
        FirebaseFirestore.getInstance().collection("users").document(idUser).update("status",0);
    }




}