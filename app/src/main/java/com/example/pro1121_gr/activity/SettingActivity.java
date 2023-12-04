package com.example.pro1121_gr.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.DesignPattern.UserSingleton;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivitySettingBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.function.MyApplication;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding binding;
    private userModel userModel;

    private NetworkChangeReceiver networkChangeReceiver;

    String idUser;
    private String currentLanguage = ""; // Ngôn ngữ hiện tại, mặc định là tiếng Anh ("en")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LanguageHelper.loadLocale(this);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        setInformation();


    }


    private void initView(){
        idUser = UserDAO.currentUserId();
        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
        MyApplication.applyNightMode();
        binding.backFragmentMess.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.option.btnNightMode.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, NightModeActivity.class));
        });


        binding.option.btnUsedTime.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity.this, UsageTimeStatisticsActivity.class));
        });
        //nút chỉnh sửa thông tin cá nhân
        binding.option.editProfile.setOnClickListener(view ->
                startActivity(new Intent(SettingActivity.this, EditProfileActivity.class)));

        binding.option.changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, ChangeLanguageActivity.class));
            }
        });

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

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        // Save any necessary data here, including language settings
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        preferences.edit().putString("lang", getCurrentLanguage()).apply();
//    }
//
//    private String getCurrentLanguage() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        return preferences.getString("lang", "");
//    }
//
//    protected void setLocale() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String language = preferences.getString("lang", "");
//
//        // Cập nhật ngôn ngữ ngay sau khi SharedPreferences thay đổi
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//
//        Resources resources = getResources();
//        Configuration configuration = resources.getConfiguration();
//        configuration.setLocale(locale);
//
//        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//    }


    @SuppressLint("LogNotTimber")
    private void setInformation() {
        // gán avt và full name
        try {
            binding.profile.fullName.setText(UserSingleton.getInstance().getUser().getUsername());
            UserDAO.setAvatar(SettingActivity.this, UserSingleton.getInstance().getUrlAVT(), binding.profile.itemAvatar);
        } catch (Exception e){
            Log.e(SettingActivity.class.getSimpleName(), Objects.requireNonNull(e.getMessage()));
            UserDAO.currentUserDetails().get().addOnCompleteListener(this, new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        userModel = task.getResult().toObject(userModel.class);
                        if (userModel != null) {
                            binding.profile.fullName.setText(userModel.getUsername());
                            // xu ly avt
                            UserDAO.getCurrentOtherProfileImageStorageReference(userModel.getUserId())
                                    .getDownloadUrl().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful())
                                            UserDAO.setAvatar(SettingActivity.this, task1.getResult(), binding.profile.itemAvatar);
                                    });
                            if (userModel.getStatus() == 0){
                                UserDAO.setOnline();
                            }
                        }
                    }
                }
            });
        }
    }


        private void logOut () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bạn có chắc chắn muốn đăng xuất không?");
            builder.setIcon(R.drawable.baseline_warning_24);

            // Nút "Có"
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("users").document(Functions.getIdUser()).update("status", 0).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Functions.setIdUser("");
                    }
                });
                // delete fcm token
                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        UserDAO.logout();
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
    }


}