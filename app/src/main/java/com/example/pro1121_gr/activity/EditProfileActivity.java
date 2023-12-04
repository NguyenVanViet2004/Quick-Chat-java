package com.example.pro1121_gr.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.DAO.UserDAO;
import com.example.pro1121_gr.Database.DBhelper;
import com.example.pro1121_gr.DesignPattern.UserSingleton;
import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityEditProfileBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.NetworkChangeReceiver;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;
    private userModel userModel;
    private NetworkChangeReceiver networkChangeReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerImagePicker();
        initView();
        checkInformation();
    }


    private void initView() {
        networkChangeReceiver = Functions.getNetworkChangeReceiver(this);
        editProfile();
        binding.backEdit.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.itemAvatar.setOnClickListener(view -> ImagePicker.with(EditProfileActivity.this).cropSquare().compress(512).maxResultSize(512,512)
                .createIntent(new Function1<Intent, Unit>() {
                    @Override
                    public Unit invoke(Intent intent) {
                        imagePickerLauncher.launch(intent);
                        return Unit.INSTANCE;
                    }
                }));

        binding.birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null) {
                    UserDAO.getCurrentProfileImageStorageReference().putFile(selectedImageUri).addOnCompleteListener(task ->{
                        if (editProfile()) setInformation();
                    });
                }else {
                    if (editProfile()) setInformation();
                    else Toasty.error(EditProfileActivity.this, "Cập nhật thông tin thất bại!", Toasty.LENGTH_LONG, true).show();
                }
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            checkInformation();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void setInformation(){
        userModel.setUsername(binding.fullName.getText().toString().trim());
        userModel.setPhone(binding.phoneNumber.getText().toString().trim());
        userModel.setDate(binding.birthday.getText().toString().trim());
        UserDAO.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(EditProfileActivity.this, R.string.update_profile_status, Toasty.LENGTH_LONG, true).show();
                    UserSingleton.getInstance().getData(EditProfileActivity.this);
                } else {
                    Toasty.error(EditProfileActivity.this, R.string.update_profile_status_failed, Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

    private boolean editProfile() {
        Functions.isEmpty(binding.fullName, 0);
        Functions.isEmpty(binding.birthday, 1);
        Functions.isEmpty(binding.phoneNumber, 2);

        if (Functions.isValidPhoneNumber(binding.phoneNumber.getText().toString())) return false;
        else if (Functions.isValidDateFormat(binding.birthday.getText().toString())) return false;
        else return binding.fullName.length() >= 5;
    }

    @SuppressLint("LogNotTimber")
    private void checkInformation() {
        // get avt
        try {
            userModel = UserSingleton.getInstance().getUser();
            UserDAO.setAvatar(EditProfileActivity.this, UserSingleton.getInstance().getUrlAVT(), binding.itemAvatar);
        } catch (Exception e) {
            Log.e(EditProfileActivity.class.getSimpleName(), Objects.requireNonNull(e.getMessage()));
            UserDAO.getCurrentProfileImageStorageReference().getDownloadUrl().addOnCompleteListener(this, task -> {
                if (task.isSuccessful())
                    UserDAO.setAvatar(EditProfileActivity.this, task.getResult(), binding.itemAvatar);
                else
                    Toasty.error(EditProfileActivity.this, R.string.error, Toasty.LENGTH_LONG, true).show();
            });

            UserDAO.currentUserDetails().get().addOnCompleteListener(EditProfileActivity.this, task -> {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(userModel.class);
                }
            });
        } finally {
            if (userModel != null) {
                binding.fullName.setText(userModel.getUsername());
                binding.birthday.setText(userModel.getDate());
                binding.phoneNumber.setText(userModel.getPhone());
            }
        }
    }

    private void registerImagePicker() {
        // Đăng ký ActivityResultLauncher để chọn hình ảnh
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Xử lý kết quả khi người dùng đã chọn hình ảnh thành công
                Intent data = result.getData();
                Uri selectedImageUriTemp = (data != null) ? data.getData() : null;

                if (data != null && selectedImageUriTemp != null) {
                    selectedImageUri = selectedImageUriTemp;
                    UserDAO.setAvatar(EditProfileActivity.this, selectedImageUri, binding.itemAvatar);
                }
            }
        });
    }


    private  void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedDate = selectedDate.format(formatter);
                binding.birthday.setText(formattedDate);
                Log.e("TAG", "onDateSet: "+formattedDate );
            }
        },
                year, month, day
        );
        datePickerDialog.show();
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