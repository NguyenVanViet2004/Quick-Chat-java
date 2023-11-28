package com.example.pro1121_gr.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityCreateProfileBinding;
import com.example.pro1121_gr.function.LoadingDialog;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class CreateProfile extends AppCompatActivity {

    private ActivityCreateProfileBinding binding;
    Button btnLoginNextEnter;
    EditText edtAge,fullname;
    String phoneNumber;
    String phoneNumberNoCode;

    userModel model;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnLoginNextEnter = findViewById(R.id.btn_loginNextEnter);
        edtAge = findViewById(R.id.edt_age);
        loadingDialog = LoadingDialog.getInstance(this);
        phoneNumber = getIntent().getStringExtra("phone");
        phoneNumberNoCode = getIntent().getStringExtra("phoneAndNoCodeCountry");
        getData();

        binding.fullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.isEmpty(binding.fullName,0);
            }
        });

        binding.edtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Functions.isEmpty(binding.edtAge, 1);
                showDatePickerDialog();
            }
        });

        binding.btnLoginNextEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setData();
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

        if(model!=null){
            model.setUsername(userName);
            model.setDate(date);
            model.setStatus(1);
        }else{
            model = new userModel(phoneNumberNoCode,userName, Timestamp.now(),date, FirebaseUtil.currentUserId());
        }
        loadingDialog.startLoading();
        FirebaseUtil.currentUserDetails().set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingDialog.isDismiss();
                    Intent intent = new Intent(CreateProfile.this, homeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK & Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    loadingDialog.isDismiss();
                    Functions.showSnackBar(binding.getRoot(), "Error, please try again!");
                }
            }
        });
    }

    void getData() {
        loadingDialog.startLoading();
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                model = task.getResult().toObject(userModel.class);
                if (model != null && !model.getUsername().isEmpty()
                        && !model.getDate().isEmpty()
                        && !model.getPhone().isEmpty()) {
                    loadingDialog.isDismiss();
                    startActivity(new Intent(CreateProfile.this, homeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else loadingDialog.isDismiss();
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

}