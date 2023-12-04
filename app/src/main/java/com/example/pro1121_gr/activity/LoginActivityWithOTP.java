package com.example.pro1121_gr.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.pro1121_gr.R;
import com.example.pro1121_gr.databinding.ActivityLoginWithOtpBinding;
import com.example.pro1121_gr.function.Functions;
import com.example.pro1121_gr.function.MyApplication;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginActivityWithOTP extends AppCompatActivity {

    private ActivityLoginWithOtpBinding binding;
    private String phoneNumber;
    private String phoneNumberNoCode;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken reResendingToken;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Dialog isDialog;


    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginWithOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Bật chế độ tối nếu được kích hoạt
        MyApplication.applyNightMode();

        // Khởi tạo LoadingDialog



        EditText otpInput = findViewById(R.id.edt_loginOTP);

        phoneNumber = getIntent().getStringExtra("phone");
        phoneNumberNoCode = getIntent().getStringExtra("phoneAndNoCodeCountry");
        binding.titleLoginOTP.setText("VUI LÒNG NHẬP MÃ OTP ĐÃ ĐƯỢC GỬI ĐẾN : " + phoneNumberNoCode);

        FirebaseFirestore.getInstance().collection("phoneNumber");
        binding.btnLoginNextOTP.setEnabled(false);

        startLoading();
        sendOTP(phoneNumber,false);
        binding.btnLoginNextOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String enteredOTP = otpInput.getText().toString();
                 PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationCode,enteredOTP);
                 signIn(credential);
                 startLoading();
            }
        });

        binding.resendOTP.setOnClickListener((v) ->{
            sendOTP(phoneNumber,true);
        });
    }
    void sendOTP( String phoneNumber,boolean isResend){
        startResendTimer();
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                            }
                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                               isDismiss();
                                Functions.Toasty(LoginActivityWithOTP.this,"OTP verification failed!", Functions.error);
                            }
                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                isDismiss();
                                binding.btnLoginNextOTP.setEnabled(true);
                                verificationCode = s;
                                reResendingToken = forceResendingToken;
                                Functions.Toasty(LoginActivityWithOTP.this,"OTP verification successfully!", Functions.success);
                            }
                        });
        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(reResendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

        }
    }
    void signIn(PhoneAuthCredential phoneAuthCredential){
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            isDismiss();
            if(task.isSuccessful()){
                Intent intent = new Intent(LoginActivityWithOTP.this,CreateProfile.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("phone",phoneNumber);
                intent.putExtra("phoneAndNoCodeCountry",phoneNumberNoCode);
                startActivity(intent);
            }else {
                Functions.Toasty(LoginActivityWithOTP.this,"OTP verification failed!", Functions.error);
            }
        });
    }

    //thời gian đếm ngược mã OTP
    void startResendTimer(){
        binding.resendOTP.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> {
                    binding.resendOTP.setText("Gửi lại mã OTP trong: " + timeoutSeconds + "s");
                });
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        binding.resendOTP.setEnabled(true);
                        binding.resendOTP.setText("Gửi lại mã OTP");
                    });
                }
            }
        }, 0, 1000);
    }

    public void startLoading() {
        // set view
        android.view.LayoutInflater inflater = getLayoutInflater();
        android.view.View dialogView = inflater.inflate(R.layout.loading_item, null);

        // set dialog
        Dialog builder = new Dialog(LoginActivityWithOTP.this);
        builder.setContentView(dialogView);
        builder.setCancelable(false);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.show();
        isDialog = builder;
    }

    public void isDismiss() {
        if (isDialog != null && isDialog.isShowing()) {
            isDialog.dismiss();
        }
    }



}