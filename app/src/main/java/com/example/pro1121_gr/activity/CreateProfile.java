package com.example.pro1121_gr.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityCreateProfileBinding;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class CreateProfile extends AppCompatActivity {

    private ActivityCreateProfileBinding binding;
    Button btnLoginNextEnter;
    EditText edtAge,fullname;
    String phoneNumber;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri selectedImageUri;

    userModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnLoginNextEnter = findViewById(R.id.btn_loginNextEnter);
        edtAge = findViewById(R.id.edt_age);

        phoneNumber = getIntent().getStringExtra("phone");
        getData();
        registerImagePicker();


        binding.edtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        binding.btnLoginNextEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedImageUri != null) {
                    firebaseUtil.getCurrentProfileImageStorageReference().putFile(selectedImageUri)
                            .addOnCompleteListener(task -> {
                                setData();
                            });
                } else {
                    setData();
                }
            }
        });

        binding.imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ImagePicker.Companion.with(CreateProfile.this)
                            .cropSquare()
                            .compress(512)
                            .maxResultSize(512, 512)
                            .createIntent(intent -> {
                                imagePickerLauncher.launch(intent);
                                return null;
                            });
                } catch (Exception e) {
                    Log.e(CreateProfile.class.getSimpleName(), e.getMessage() );
                }

            }
        });
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
                    firebaseUtil.setAvatar(CreateProfile.this, selectedImageUri, binding.imgAvatar);
                }
            }
        });
    }


    void setData(){
        String userName = binding.fullName.getText().toString();
        String date =  binding.edtAge.getText().toString();

        if(userName.isEmpty() || userName.length() < 3){
            fullname.setError(" Username toi thieu phai co 3 ki tu!");
            return;
        }
        if(date.isEmpty()) {
            edtAge.setError("Ngày tháng năm sinh không được bỏ trống !");
            return;
        }

        setInProgress(true);

        if(model!=null){
            model.setUsername(userName);
            model.setDate(date);
        }else{
            model = new userModel(phoneNumber,userName, Timestamp.now(),date, firebaseUtil.currentUserId());
        }
        firebaseUtil.currentUserDetails().set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(CreateProfile.this, home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    Toasty.error(CreateProfile.this,"Đã có lỗi xảy ra! Vui lòng thử lại sau.", Toasty.LENGTH_LONG, true).show();
                }
            }
        });
    }

    void getData(){
        setInProgress(true);
        firebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    model  = task.getResult().toObject(userModel.class);
                    if(model!=null){
                        startActivity(new Intent(CreateProfile.this, home.class));
                        finish();
                    }
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
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                LocalDate selectedDate = LocalDate.of(year, month + 1, day);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String formattedDate = selectedDate.format(formatter);
                binding.edtAge.setText(formattedDate);
            }
        },
                year, month, day
        );
        datePickerDialog.show();
    }
    void setInProgress(boolean inProgress){
        if(inProgress){
            btnLoginNextEnter.setVisibility(View.GONE);
        }else{
            btnLoginNextEnter.setVisibility(View.VISIBLE);
        }
    }

}