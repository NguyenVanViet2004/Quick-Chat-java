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
import com.example.pro1121_gr.function.StaticFunction;
import com.example.pro1121_gr.model.userModel;
import com.example.pro1121_gr.util.firebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class CreateProfile extends AppCompatActivity {

    private ActivityCreateProfileBinding binding;
    Button btnLoginNextEnter;
    EditText edtAge,fullname;
    String phoneNumber;

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
        getData();


        binding.edtAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        }else{
            model = new userModel(phoneNumber,userName, Timestamp.now(),date, firebaseUtil.currentUserId());
        }
        loadingDialog.startLoading();
        firebaseUtil.currentUserDetails().set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingDialog.isDismiss();
                    Intent intent = new Intent(CreateProfile.this, home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else{
                    loadingDialog.isDismiss();
                    StaticFunction.showError(CreateProfile.this);
                }
            }
        });
    }

    void getData() {
        loadingDialog.startLoading();
        firebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                model = task.getResult().toObject(userModel.class);
                if (model != null) {
                    loadingDialog.isDismiss();
                    startActivity(new Intent(CreateProfile.this, home.class));
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
    void setInProgress(boolean inProgress){
        if(inProgress){
            btnLoginNextEnter.setVisibility(View.GONE);
        }else{
            btnLoginNextEnter.setVisibility(View.VISIBLE);
        }
    }

}